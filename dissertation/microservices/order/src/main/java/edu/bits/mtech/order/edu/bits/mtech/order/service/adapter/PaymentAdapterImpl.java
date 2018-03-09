/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.edu.bits.mtech.order.service.adapter;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.order.db.bo.Order;
import edu.bits.mtech.order.db.bo.Payment;
import edu.bits.mtech.order.edu.bits.mtech.order.service.adapter.bo.AuthorizePaymentRequest;
import edu.bits.mtech.order.edu.bits.mtech.order.service.adapter.bo.AuthorizePaymentResponse;
import edu.bits.mtech.order.edu.bits.mtech.order.service.bo.PaymentInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adapter implementation for payment.
 *
 * @author Tushar Phadke
 */
@Service
public class PaymentAdapterImpl implements PaymentAdapter {

    private static final Logger logger = Logger.getLogger(PaymentAdapterImpl.class.getName());

    private static final String SERVICE_URL = "http://"+ BitsPocConstants.PAYMENT_SERVICE.toUpperCase()+"/rest/payment/authorize";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public AuthorizePaymentResponse authorizePayment(Order order, PaymentInformation paymentInformation) {
        logger.info("Calling Authorize payment");
        AuthorizePaymentResponse response = null;
        ResponseEntity<AuthorizePaymentResponse> authorizePaymentResponseResponseEntity = null;
        try {
            AuthorizePaymentRequest request = populateRequestObject(order, paymentInformation);


            authorizePaymentResponseResponseEntity =
                    restTemplate.postForEntity(SERVICE_URL, request, AuthorizePaymentResponse.class);

            if (authorizePaymentResponseResponseEntity != null &&
                    authorizePaymentResponseResponseEntity.getStatusCode() == HttpStatus.ACCEPTED) {
                response = authorizePaymentResponseResponseEntity.getBody();
            }
            logger.info("Authorize Payment is successful: " + response);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to execute authorize payment", e);
        }

        return response;
    }

    private AuthorizePaymentRequest populateRequestObject(Order order, PaymentInformation paymentInformation) {
        AuthorizePaymentRequest request = new AuthorizePaymentRequest();
        request.setAuthorizeAmount(paymentInformation.getPaymentAmt());
        request.setCardNumber(paymentInformation.getCardNumber());
        request.setCustomerId(order.getCustomerId());
        request.setCvv(paymentInformation.getCvv());
        request.setNameOnCard(paymentInformation.getNameOnCard());
        request.setOrderId(order.getOrderId());

        return request;
    }
}
