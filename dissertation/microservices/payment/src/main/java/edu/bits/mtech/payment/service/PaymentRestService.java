/**
 * 
 */
package edu.bits.mtech.payment.service;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.StatusEnum;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.common.bo.EventData;
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

	@RequestMapping(method = RequestMethod.POST, value = "/rest/payment/authorize", produces = "application/json",
			consumes = "application/json")
	public ResponseEntity<AuthorizePaymentResponse> authorizePayment(@RequestBody
                                                  @Valid @Validated AuthorizePaymentRequest request) {


		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Authorize Payment request received: " + request.getOrderId());
		}

		AcquirerAuthorizeResponse authorizeResponse = authorizePaymentWithAcquirer(request);

		AuthorizePaymentResponse response = new AuthorizePaymentResponse();
		if (authorizeResponse == null) {
			response.setMessage("Failed to authorize");
			response.setStatusCode(HttpStatus.BAD_REQUEST);

			//Fire event on queue for failed authorize
			firePaymentEvent(request, null, StatusEnum.PAYMENT_FAILED);
			ResponseEntity.accepted();
		} else {
			response.setPaymentId(authorizeResponse.getAuthorizeId());
			response.setStatusCode(HttpStatus.ACCEPTED);

			//fire event on queue for successful authorize
			firePaymentEvent(request, authorizeResponse.getAuthorizeId(), StatusEnum.PAYMENT_AUTHORIZED);
		}

		return ResponseEntity.ok(response);
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
	private AcquirerAuthorizeResponse authorizePaymentWithAcquirer(AuthorizePaymentRequest request) {
		AcquirerAuthorizeResponse authorizeResponse = null;

		try {
			logger.info("calling acquirer service");
			AcquirerAuthorizeRequest acquirerAuthorizeRequest = new AcquirerAuthorizeRequest();
			acquirerAuthorizeRequest.setCardNumber(request.getCardNumber());
			acquirerAuthorizeRequest.setNameOnCard(request.getNameOnCard());
			acquirerAuthorizeRequest.setCvv(request.getCvv());
			acquirerAuthorizeRequest.setAuthorizeAmount(request.getAuthorizeAmount());
			authorizeResponse = acquirerServiceAdapter.authorize(acquirerAuthorizeRequest);

			logger.info("Response from Acquirer: " + authorizeResponse);

		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to call acquirer ", e);
		}
		return authorizeResponse;
	}
}
