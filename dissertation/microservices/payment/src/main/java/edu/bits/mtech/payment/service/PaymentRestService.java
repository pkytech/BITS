/**
 * 
 */
package edu.bits.mtech.payment.service;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.StatusEnum;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.payment.db.bo.Order;
import edu.bits.mtech.payment.db.bo.Payment;
import edu.bits.mtech.payment.db.repository.PaymentRepository;
import edu.bits.mtech.payment.kafka.PaymentEventProducer;
import edu.bits.mtech.payment.service.adapter.AcquirerServiceAdapter;
import edu.bits.mtech.payment.service.bo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST Service for payment.
 * 
 * @author Tushar Phadke
 */
@RestController
public class PaymentRestService {

	private static final Logger logger = Logger.getLogger(PaymentRestService.class.getName());

	@Autowired
	private PaymentEventProducer paymentEventProducer;

	@Autowired
	private AcquirerServiceAdapter acquirerServiceAdapter;

	@Autowired
    private PaymentRepository paymentRepository;

	@RequestMapping(value = "/rest/payment/{paymentId}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaymentResponse> getOrder(@PathVariable("paymentId") String paymentId) {

		Payment payment = paymentRepository.findPaymentByKey(paymentId);
		if (payment == null) {
			return ResponseEntity.notFound().build();
		}
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setPaymentId(payment.getPaymentId());
		paymentResponse.setAuthorizeAmount(payment.getAuthorizeAmount());
		paymentResponse.setBillNumber(payment.getBillNumber());
		paymentResponse.setPaymentAmount(payment.getPaymentAmount());
		paymentResponse.setCustomerId(payment.getCustomerId());
		paymentResponse.setStatus(payment.getStatus());
		paymentResponse.setOrderId(payment.getOrder().getOrderId());
		paymentResponse.setOrderStatus(payment.getOrder().getStatus());

		return ResponseEntity.ok(paymentResponse);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/rest/payment/capture", produces = "application/json"
			, consumes = "application/json")
	public ResponseEntity<AuthorizePaymentResponse> capture(@RequestBody
														  @Valid @Validated AuthorizePaymentRequest request) {

		logger.info("Capture Payment called: " +request);

		Payment payment = paymentRepository.findPaymentByKey(request.getPaymentId());
		AuthorizePaymentResponse response = new AuthorizePaymentResponse();
		if (payment == null) {
			logger.fine("Payment not found");
			return ResponseEntity.notFound().build();
		}
		if (StatusEnum.PAYMENT_AUTHORIZED.name().equalsIgnoreCase(payment.getStatus())) {
			double authAmt = payment.getAuthorizeAmount();

			if (authAmt <= request.getCaptureAmount()) {
				payment.setPaymentAmount(request.getCaptureAmount());
				payment.setStatus(StatusEnum.PAYMENT_CAPTURED.name());

				//save to DB
				try {
					paymentRepository.update(payment);

					firePaymentEvent(request.getOrderId(), payment.getPaymentId(), request.getBillId(),
							request.getAuthorizeAmount(), StatusEnum.PAYMENT_CAPTURED);
					response.setStatusCode(HttpStatus.ACCEPTED);
					response.setCaptureAmount(request.getCaptureAmount());

				} catch (Exception e) {
					logger.log(Level.WARNING, "Failed to update payment", e);
					response.setStatusCode(HttpStatus.BAD_REQUEST);
					response.setMessage("Failed to update Payment. Contact administrator");
				}
			} else {
				response.setStatusCode(HttpStatus.BAD_REQUEST);
				response.setMessage("Capture amount does not match with authroize amount");
			}
		} else {
			response.setStatusCode(HttpStatus.BAD_REQUEST);
			response.setMessage("Payment is in invalid state: " + payment.getStatus());
		}

		return ResponseEntity.status(response.getStatusCode()).body(response);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/rest/payment/authorize", produces = "application/json",
			consumes = "application/json")
	public ResponseEntity<AuthorizePaymentResponse> authorizePayment(@RequestBody
                                                  @Valid @Validated AuthorizePaymentRequest request) {


		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Authorize Payment request received: " + request.getOrderId());
		}

		//Check for idempotency
		AcquirerAuthorizeResponse authorizeResponse = null;

        Payment payment = null;
        if (request.getPaymentId() != null) {
			payment = paymentRepository.findPaymentByKey(request.getPaymentId());
		}
		if (payment != null && StatusEnum.PAYMENT_AUTHORIZED.name().equalsIgnoreCase(payment.getStatus())) {
            AuthorizePaymentResponse response = new AuthorizePaymentResponse();
            response.setPaymentId(payment.getPaymentId());
            response.setAuthorizeAmount(payment.getAuthorizeAmount());
            response.setOrderId(request.getOrderId());
		    return ResponseEntity.ok(response);
        } else if (payment != null && payment.getAuthorizeAmount() < request.getAuthorizeAmount()) {
		    logger.info("Updating authorize amount");
            authorizeResponse = authorizePaymentWithAcquirer(request, payment.getPaymentId());
        } else {
            logger.info("Authorize amount");
            authorizeResponse = authorizePaymentWithAcquirer(request, null);
        }

		AuthorizePaymentResponse response = new AuthorizePaymentResponse();
		if (authorizeResponse == null) {
			response.setMessage("Failed to authorize");
			response.setStatusCode(HttpStatus.BAD_REQUEST);
			response.setPaymentStatusCode(StatusEnum.PAYMENT_AUTHORIZE_FAILED);

			//Fire event on queue for failed authorize
			firePaymentEvent(request.getOrderId(), null, request.getBillId(),
					request.getAuthorizeAmount(), StatusEnum.PAYMENT_AUTHORIZE_FAILED);

			return ResponseEntity.badRequest().body(response);
		} else {
			response.setPaymentId(authorizeResponse.getAuthorizeId());
			response.setStatusCode(HttpStatus.ACCEPTED);
            response.setPaymentStatusCode(StatusEnum.PAYMENT_AUTHORIZED);
            response.setAuthorizeAmount(authorizeResponse.getAuthorizeAmount());
            if (request.getCaptureAmount() > 0) {
            	response.setPaymentStatusCode(StatusEnum.PAYMENT_CAPTURED);
			}

            //save to DB
            boolean saveSuccessful = false;
            try {
                logger.fine("Saving Payment entity");
                saveToDatabase(request, authorizeResponse);
                saveSuccessful = true;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to save PaymentEntity calling cancel authorize", e);
                response.setPaymentStatusCode(StatusEnum.PAYMENT_AUTHORIZE_FAILED);
                AcquirerAuthorizeResponse cancelAuthorize = cancelAuthorize(authorizeResponse.getAuthorizeId());
                if (cancelAuthorize == null) {
                    logger.log(Level.SEVERE, "PAY001: Failed to cancel auth: " + authorizeResponse.getAuthorizeId());
                }

				firePaymentEvent(request.getOrderId(), authorizeResponse.getAuthorizeId(), request.getBillId(),
						request.getAuthorizeAmount(),
						StatusEnum.PAYMENT_AUTHORIZE_FAILED);
            }

            //fire event on queue for successful authorize
            if (saveSuccessful) {
                firePaymentEvent(request.getOrderId(), authorizeResponse.getAuthorizeId(), request.getBillId(),
						request.getAuthorizeAmount(),
						request.getCaptureAmount() > 0 ? StatusEnum.PAYMENT_CAPTURED : StatusEnum.PAYMENT_AUTHORIZED);
            }

		}

		return ResponseEntity.accepted().body(response);
	}

    private AcquirerAuthorizeResponse cancelAuthorize(String authorizeId) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Cancel Authorize Payment request received: " + authorizeId);
        }

        return acquirerServiceAdapter.cancelAuthorize(authorizeId);
    }

    private void saveToDatabase(AuthorizePaymentRequest request, AcquirerAuthorizeResponse authorizeResponse) {
        Payment payment = new Payment();
        payment.setPaymentId(authorizeResponse.getAuthorizeId());
        payment.setCardNumber(request.getCardNumber());
        payment.setNameOnCard(request.getNameOnCard());
        payment.setCvv(request.getCvv());
        payment.setCustomerId(request.getCustomerId());
        payment.setBillNumber(request.getBillId());
        payment.setPaymentId(authorizeResponse.getAuthorizeId());
        payment.setAuthorizeAmount(authorizeResponse.getAuthorizeAmount());
        payment.setPaymentAmount(request.getCaptureAmount());
        if (request.getCaptureAmount() > 0) {
        	payment.setStatus(StatusEnum.PAYMENT_CAPTURED.name());
		} else {
        	payment.setStatus(StatusEnum.PAYMENT_AUTHORIZED.name());
		}

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderId(request.getOrderId());
        order.setPaymentId(payment.getPaymentId());

        payment.setOrder(order);

        paymentRepository.save(payment);

        logger.info("Payment successfully saved to DB");
    }

    private void firePaymentEvent(String orderId, String paymentId, String billId, double authorizeAmt,
								  StatusEnum paymentStatus) {
		Event event = new Event();
		event.setEventId(UUID.randomUUID().toString());
		event.setSource(BitsPocConstants.PAYMENT_SERVICE.toUpperCase());
		event.setOrderId(orderId);
		event.setBillId(billId);
		event.setAuthorizeAmount(authorizeAmt);
		event.setPaymentId(paymentId);
		event.setStatus(paymentStatus);
		paymentEventProducer.produceEvent(event);
	}

	/**
	 * Authorize payment with acquirer.
	 *
	 * @param request authorize request from client
	 * @return the response from acquirer.
	 */
	private AcquirerAuthorizeResponse authorizePaymentWithAcquirer(AuthorizePaymentRequest request, String paymentId) {
		AcquirerAuthorizeResponse authorizeResponse = null;

		try {
			logger.info("calling acquirer service");
			AcquirerAuthorizeRequest acquirerAuthorizeRequest = new AcquirerAuthorizeRequest();
			acquirerAuthorizeRequest.setCardNumber(request.getCardNumber());
			acquirerAuthorizeRequest.setNameOnCard(request.getNameOnCard());
			acquirerAuthorizeRequest.setCvv(request.getCvv());
			acquirerAuthorizeRequest.setAuthorizeAmount(request.getAuthorizeAmount());
			if (paymentId != null) {
			    acquirerAuthorizeRequest.setPaymentId(paymentId);
            }
			authorizeResponse = acquirerServiceAdapter.authorize(acquirerAuthorizeRequest, paymentId == null ? false : true);

			logger.info("Response from Acquirer: " + authorizeResponse);

		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to call acquirer ", e);
		}
		return authorizeResponse;
	}

	@RequestMapping(value = "/rest/payment/{paymentId}/{status}", method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaymentResponse> acceptOrRejectPayment(@PathVariable("paymentId") String paymentId,
																 @PathVariable("status") String status) {
		logger.info("Payment confirmation by acquirer: " + paymentId + ", status: " + status);
		Payment payment = paymentRepository.findPaymentByKey(paymentId);
		if (payment == null) {
			return ResponseEntity.notFound().build();
		}
		if (BitsPocConstants.CONFIRM.equalsIgnoreCase(status)) {
			payment.setStatus(StatusEnum.PAYMENT_SUCCESSFUL.name());

			try {
				paymentRepository.update(payment);

				firePaymentEvent(payment.getOrder().getOrderId(), paymentId, payment.getBillNumber(),
						payment.getAuthorizeAmount(), StatusEnum.PAYMENT_SUCCESSFUL);
			} catch(Exception e) {
				logger.log(Level.WARNING, "Failed to save payment");
			}
		} else if (BitsPocConstants.REJECT.equalsIgnoreCase(status)) {
			payment.setStatus(StatusEnum.PAYMENT_REJECTED.name());

			try {
				paymentRepository.update(payment);

				firePaymentEvent(payment.getOrder().getOrderId(), paymentId, payment.getBillNumber(),
						payment.getAuthorizeAmount(), StatusEnum.PAYMENT_REJECTED);
			} catch(Exception e) {
				logger.log(Level.WARNING, "Failed to save payment");
			}
		} else {
			PaymentResponse response = new PaymentResponse();
			response.setMessage("Wrong status send. Please send a valid status");
			return ResponseEntity.badRequest().body(response);
		}
		PaymentResponse response = new PaymentResponse();
		response.setPaymentId(paymentId);
		response.setStatus(payment.getStatus());
		response.setOrderId(payment.getOrder().getOrderId());
		return ResponseEntity.ok(response);
	}
}
