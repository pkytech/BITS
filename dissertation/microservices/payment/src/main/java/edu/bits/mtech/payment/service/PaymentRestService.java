/**
 * 
 */
package edu.bits.mtech.payment.service;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.StatusEnum;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.common.bo.EventData;
import edu.bits.mtech.payment.db.bo.Order;
import edu.bits.mtech.payment.db.bo.Payment;
import edu.bits.mtech.payment.db.repository.PaymentRepository;
import edu.bits.mtech.payment.kafka.PaymentEventProducer;
import edu.bits.mtech.payment.service.adapter.AcquirerServiceAdapter;
import edu.bits.mtech.payment.service.bo.AcquirerAuthorizeRequest;
import edu.bits.mtech.payment.service.bo.AcquirerAuthorizeResponse;
import edu.bits.mtech.payment.service.bo.AuthorizePaymentRequest;
import edu.bits.mtech.payment.service.bo.AuthorizePaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

	@RequestMapping(method = RequestMethod.POST, value = "/rest/payment/authorize", produces = "application/json",
			consumes = "application/json")
	public ResponseEntity<AuthorizePaymentResponse> authorizePayment(@RequestBody
                                                  @Valid @Validated AuthorizePaymentRequest request) {


		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Authorize Payment request received: " + request.getOrderId());
		}

        AcquirerAuthorizeResponse authorizeResponse = null;

		//Check for idempotency
        Order order = new Order();
        order.setOrderId(request.getOrderId());
        Payment payment = null;//paymentRepository.findByOrder(order);
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
			firePaymentEvent(request, null, StatusEnum.PAYMENT_AUTHORIZE_FAILED);

			return ResponseEntity.badRequest().body(response);
		} else {
			response.setPaymentId(authorizeResponse.getAuthorizeId());
			response.setStatusCode(HttpStatus.ACCEPTED);
            response.setPaymentStatusCode(StatusEnum.PAYMENT_AUTHORIZED);
            response.setAuthorizeAmount(authorizeResponse.getAuthorizeAmount());

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
            }

            //fire event on queue for successful authorize
            if (saveSuccessful) {
                firePaymentEvent(request, authorizeResponse.getAuthorizeId(), StatusEnum.PAYMENT_AUTHORIZED);
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
        payment.setPaymentId(authorizeResponse.getAuthorizeId());
        payment.setAuthorizeAmount(authorizeResponse.getAuthorizeAmount());

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderId(request.getOrderId());
        order.setPaymentId(payment.getPaymentId());

        payment.setOrder(order);

        paymentRepository.save(payment);

        logger.info("Payment successfully saved to DB");
    }

    private void firePaymentEvent(AuthorizePaymentRequest request, String paymentId,
								  StatusEnum paymentStatus) {
		Event event = new Event();
		event.setEventId(UUID.randomUUID().toString());
		event.setSource(BitsPocConstants.PAYMENT_SERVICE.toUpperCase());
		EventData data = new EventData();
		data.setOrderId(request.getOrderId());
		data.setPaymentId(paymentId);
		data.setStatus(paymentStatus);
		event.setData(data);
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
}
