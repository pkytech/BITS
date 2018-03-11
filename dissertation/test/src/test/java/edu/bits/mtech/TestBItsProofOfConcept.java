/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */
package edu.bits.mtech;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bits.mtech.bo.*;
import edu.bits.mtech.common.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import static org.testng.Assert.*;
import org.testng.annotations.Test;


/**
 * Test class for testing BITS Proof Of Concept
 *
 * @author Tushar Phadke
 */
public class TestBItsProofOfConcept {

    private static final String PAYMENT_SERVER = BitsConfigurator.getProperty("bits.mtech.payment.server");
    private static final String ORDER_SERVER = BitsConfigurator.getProperty("bits.mtech.order.server");
    private static final String ORDER_CREATE = ORDER_SERVER+"/rest/order";
    private static final String PAYMENT_URL = PAYMENT_SERVER + "/rest/payment/";
    private static final String GET_ORDER = ORDER_CREATE+"/";

    @Test
    public void testPositiveScenario_OrderCreationWith_PaymentConfirmFromAcquirer() {

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBillId("1234");
        orderRequest.setCustomerId(112233);
        orderRequest.setOrderAmt(6200.75);
        orderRequest.addOrderLineItem("Phone Battery", 3, 2000.25, 6000.75);
        orderRequest.addOrderLineItem("Recharge", 1, 200, 200);
        PaymentInformation paymentInformation = new PaymentInformation();
        paymentInformation.setCardNumber("1234123412341234");
        paymentInformation.setCvv("123");
        paymentInformation.setNameOnCard("Test User");
        paymentInformation.setPaymentAmt(6200.75);
        orderRequest.setPaymentInformation(paymentInformation);

        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity <OrderRequest> requestEntity = new HttpEntity <OrderRequest> (orderRequest, requestHeaders);

            RestTemplate restTemplate = TestUtil.buildRestTemplate();
            System.out.println("Order URL " +ORDER_CREATE );
            ResponseEntity<OrderResponse> entity = restTemplate.exchange(ORDER_CREATE, HttpMethod.POST, requestEntity, OrderResponse.class);

            assertNotNull(entity, "ResponseEntity should not be null");
            assertEquals(entity.getStatusCode(), HttpStatus.ACCEPTED, "Status Code not 201");
            assertNotNull(entity.getBody(), "Object not created");
            OrderResponse orderResponse = entity.getBody();

            assertEquals(orderResponse.getOrderStatus(), StatusEnum.ORDER_CONFIRMED.name(), "Order Status does not match");
            assertEquals(orderResponse.getPaymentStatus(), StatusEnum.PAYMENT_CAPTURED.name(), "Payment Status does not match");

            String orderId = orderResponse.getOrderId();
            String paymentId = orderResponse.getPaymentId();

            sleepFor(500);

            //Get Order Details
            ResponseEntity<GetOrderResponse> getOrderResponseEntity = getOrdersFromServer(restTemplate, orderId);
            assertNotNull(getOrderResponseEntity, "Response Entity should not null");
            assertEquals(getOrderResponseEntity.getStatusCode(), HttpStatus.OK, "Status Code not 200");
            assertNotNull(getOrderResponseEntity.getBody(), "Get Order response should not be null");
            GetOrderResponse getOrderResponse = getOrderResponseEntity.getBody();
            //Assert Get Details
            assertEquals(getOrderResponse.getOrderId(), orderId, "Order Amount does not match");
            assertEquals(getOrderResponse.getOrderAmt(), orderRequest.getOrderAmt());
            assertEquals(getOrderResponse.getPaymentInformation().getPaymentId(), paymentId, "Payment Id does not match");
            assertEquals(getOrderResponse.getPaymentInformation().getPaymentAmt(), paymentInformation.getPaymentAmt(), "Payment amount does not match");
            assertEquals(getOrderResponse.getPaymentInformation().getStatus(), StatusEnum.PAYMENT_CAPTURED.name(), "Payment status does not match");
            assertEquals(getOrderResponse.getOrderStatus(), StatusEnum.ORDER_CONFIRMED.name(), "Order Status does not match");


            //Get Payment Details
            ResponseEntity<PaymentResponse> paymentResponseResponseEntity = getPaymentFromServer(restTemplate, paymentId);
            assertNotNull(paymentResponseResponseEntity, "Response Entity should not null");
            assertEquals(paymentResponseResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");
            assertNotNull(paymentResponseResponseEntity.getBody(), "Get Order response should not be null");
            PaymentResponse paymentResponse = paymentResponseResponseEntity.getBody();

            //Assert Get Details
            assertEquals(paymentResponse.getOrderId(), orderId, "Order Amount does not match");
            assertEquals(paymentResponse.getOrderStatus(), StatusEnum.ORDER_CONFIRMED.name(), "Order status does not match");
            assertEquals(paymentResponse.getPaymentId(), paymentId, "Payment Id does not match");
            assertEquals(paymentResponse.getStatus(), StatusEnum.PAYMENT_CAPTURED.name(), "Payment status does not match");
            assertEquals(paymentResponse.getAuthorizeAmount(), paymentInformation.getPaymentAmt(), "Payment Amount does nor maTCH");


            //Accept the payment which mimics the response from Acquirer
            paymentResponseResponseEntity = acceptPayment(restTemplate, paymentId, BitsPocConstants.CONFIRM);
            assertNotNull(paymentResponseResponseEntity, "Response Entity should not null");
            assertEquals(paymentResponseResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");

            //Sleep and let application complete logic
            sleepFor(3000);

            //Get Order Details
            getOrderResponseEntity = getOrdersFromServer(restTemplate, orderId);
            assertNotNull(getOrderResponseEntity, "Response Entity should not null");
            assertEquals(getOrderResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");
            assertNotNull(getOrderResponseEntity.getBody(), "Get Order response should not be null");
            getOrderResponse = getOrderResponseEntity.getBody();
            //Assert Get Details
            assertEquals(getOrderResponse.getOrderId(), orderId, "Order Amount does not match");
            assertEquals(getOrderResponse.getOrderAmt(), orderRequest.getOrderAmt());
            assertEquals(getOrderResponse.getPaymentInformation().getPaymentId(), paymentId, "Payment Id does not match");
            assertEquals(getOrderResponse.getPaymentInformation().getPaymentAmt(), paymentInformation.getPaymentAmt(), "Payment amount does not match: " + paymentId);
            assertEquals(getOrderResponse.getPaymentInformation().getStatus(), StatusEnum.PAYMENT_SUCCESSFUL.name(), "Payment status does not match: " + paymentId);
            assertEquals(getOrderResponse.getOrderStatus(), StatusEnum.ORDER_CONFIRMED.name(), "Order Status does not match");

            //Get Payment object again
            paymentResponseResponseEntity = getPaymentFromServer(restTemplate, paymentId);
            assertNotNull(paymentResponseResponseEntity, "Response Entity should not null");
            assertEquals(paymentResponseResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");
            assertNotNull(paymentResponseResponseEntity.getBody(), "Get Order response should not be null");
            paymentResponse = paymentResponseResponseEntity.getBody();

            //Assert Get Details
            assertEquals(paymentResponse.getOrderId(), orderId, "Order Amount does not match");
            assertEquals(paymentResponse.getOrderStatus(), StatusEnum.ORDER_CONFIRMED.name(), "Order status does not match");
            assertEquals(paymentResponse.getPaymentId(), paymentId, "Payment Id does not match");
            assertEquals(paymentResponse.getStatus(), StatusEnum.PAYMENT_SUCCESSFUL.name(), "Payment status does not match");
            assertEquals(paymentResponse.getAuthorizeAmount(), paymentInformation.getPaymentAmt(), "Payment Amount does nor maTCH");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail("Failed to execute test", e);
        }
    }

    @Test
    public void testPositiveScenario_OrderCreationWith_PaymentRejectFromAcquirer() {

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBillId("12345");
        orderRequest.setCustomerId(11223344);
        orderRequest.setOrderAmt(28201.75);
        orderRequest.addOrderLineItem("Phone Battery", 7, 4000.25, 28001.75);
        orderRequest.addOrderLineItem("Recharge", 2, 200, 200);
        PaymentInformation paymentInformation = new PaymentInformation();
        paymentInformation.setCardNumber("1234123412341234");
        paymentInformation.setCvv("123");
        paymentInformation.setNameOnCard("Test User");
        paymentInformation.setPaymentAmt(6200.75);
        orderRequest.setPaymentInformation(paymentInformation);

        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity <OrderRequest> requestEntity = new HttpEntity <OrderRequest> (orderRequest, requestHeaders);

            RestTemplate restTemplate = TestUtil.buildRestTemplate();
            System.out.println("Order URL to call " +ORDER_CREATE );
            ResponseEntity<OrderResponse> entity = restTemplate.exchange(ORDER_CREATE, HttpMethod.POST, requestEntity, OrderResponse.class);

            assertNotNull(entity, "ResponseEntity should not be null");
            assertEquals(entity.getStatusCode(),HttpStatus.ACCEPTED,"Status Code not 201");
            assertNotNull(entity.getBody(), "Object not created");
            OrderResponse orderResponse = entity.getBody();

            assertEquals(orderResponse.getOrderStatus(), StatusEnum.ORDER_CONFIRMED.name(), "Order Status does not match");
            assertEquals(orderResponse.getPaymentStatus(), StatusEnum.PAYMENT_CAPTURED.name(), "Payment Status does not match");

            String orderId = orderResponse.getOrderId();
            String paymentId = orderResponse.getPaymentId();

            sleepFor(500);

            //Get Order Details
            ResponseEntity<GetOrderResponse> getOrderResponseEntity = getOrdersFromServer(restTemplate, orderId);
            assertNotNull(getOrderResponseEntity, "Response Entity should not null");
            assertEquals(getOrderResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");
            assertNotNull(getOrderResponseEntity.getBody(), "Get Order response should not be null");
            GetOrderResponse getOrderResponse = getOrderResponseEntity.getBody();
            //Assert Get Details
            assertEquals(getOrderResponse.getOrderId(), orderId, "Order Amount does not match");
            assertEquals(getOrderResponse.getOrderAmt(), orderRequest.getOrderAmt());
            assertEquals(getOrderResponse.getPaymentInformation().getPaymentId(), paymentId, "Payment Id does not match");
            assertEquals(getOrderResponse.getPaymentInformation().getPaymentAmt(), paymentInformation.getPaymentAmt(), "Payment amount does not match");
            assertEquals(getOrderResponse.getPaymentInformation().getStatus(), StatusEnum.PAYMENT_CAPTURED.name(), "Payment status does not match");
            assertEquals(getOrderResponse.getOrderStatus(), StatusEnum.ORDER_CONFIRMED.name(), "Order Status does not match");

            //Get Payment Details
            ResponseEntity<PaymentResponse> paymentResponseResponseEntity = getPaymentFromServer(restTemplate, paymentId);
            assertNotNull(paymentResponseResponseEntity, "Response Entity should not null");
            assertEquals(paymentResponseResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");
            assertNotNull(paymentResponseResponseEntity.getBody(), "Get Order response should not be null");
            PaymentResponse paymentResponse = paymentResponseResponseEntity.getBody();

            //Assert Get Details
            assertEquals(paymentResponse.getOrderId(), orderId, "Order Amount does not match");
            assertEquals(paymentResponse.getOrderStatus(), StatusEnum.ORDER_CONFIRMED.name(), "Order status does not match");
            assertEquals(paymentResponse.getPaymentId(), paymentId, "Payment Id does not match");
            assertEquals(paymentResponse.getStatus(), StatusEnum.PAYMENT_CAPTURED.name(), "Payment status does not match");
            assertEquals(paymentResponse.getAuthorizeAmount(), paymentInformation.getPaymentAmt(), "Payment Amount does nor maTCH");


            //Accept the payment which mimics the response from Acquirer
            paymentResponseResponseEntity = acceptPayment(restTemplate, paymentId, BitsPocConstants.REJECT);
            assertNotNull(paymentResponseResponseEntity, "Response Entity should not null");
            assertEquals(paymentResponseResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");

            //Sleep and let application complete logic
            sleepFor(3000);

            //Get Order Details
            getOrderResponseEntity = getOrdersFromServer(restTemplate, orderId);
            assertNotNull(getOrderResponseEntity, "Response Entity should not null");
            assertEquals(getOrderResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");
            assertNotNull(getOrderResponseEntity.getBody(), "Get Order response should not be null");
            getOrderResponse = getOrderResponseEntity.getBody();
            //Assert Get Details
            assertEquals(getOrderResponse.getOrderId(), orderId, "Order Amount does not match");
            assertEquals(getOrderResponse.getOrderAmt(), orderRequest.getOrderAmt());
            assertEquals(getOrderResponse.getPaymentInformation().getPaymentId(), paymentId, "Payment Id does not match");
            assertEquals(getOrderResponse.getPaymentInformation().getPaymentAmt(), paymentInformation.getPaymentAmt(), "Payment amount does not match");
            assertEquals(getOrderResponse.getPaymentInformation().getStatus(), StatusEnum.PAYMENT_REJECTED.name(), "Payment status does not match");
            assertEquals(getOrderResponse.getOrderStatus(), StatusEnum.ORDER_REJECTED.name(), "Order Status does not match");

            //Get Payment object again
            paymentResponseResponseEntity = getPaymentFromServer(restTemplate, paymentId);
            assertNotNull(paymentResponseResponseEntity, "Response Entity should not null");
            assertEquals(paymentResponseResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");
            assertNotNull(paymentResponseResponseEntity.getBody(), "Get Order response should not be null");
            paymentResponse = paymentResponseResponseEntity.getBody();

            //Assert Get Details
            assertEquals(paymentResponse.getOrderId(), orderId, "Order Amount does not match");
            assertEquals(paymentResponse.getOrderStatus(), StatusEnum.ORDER_REJECTED.name(), "Order status does not match");
            assertEquals(paymentResponse.getPaymentId(), paymentId, "Payment Id does not match");
            assertEquals(paymentResponse.getStatus(), StatusEnum.PAYMENT_REJECTED.name(), "Payment status does not match");
            assertEquals(paymentResponse.getAuthorizeAmount(), paymentInformation.getPaymentAmt(), "Payment Amount does nor maTCH");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail("Failed to execute test", e);
        }
    }

    @Test
    public void testNegativeScenario_PaymentAuthorizationFailed() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBillId("123456");
        orderRequest.setCustomerId(11223355);
        orderRequest.setOrderAmt(28201.75);
        orderRequest.addOrderLineItem("Phone Battery", 7, 4000.25, 28001.75);
        orderRequest.addOrderLineItem("Recharge", 2, 200, 200);
        PaymentInformation paymentInformation = new PaymentInformation();
        paymentInformation.setCardNumber("1234123412341234");
        paymentInformation.setCvv("1234");
        paymentInformation.setNameOnCard("Test User");
        paymentInformation.setPaymentAmt(6200.75);
        orderRequest.setPaymentInformation(paymentInformation);
        RestTemplate restTemplate = null;
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity <OrderRequest> requestEntity = new HttpEntity <OrderRequest> (orderRequest, requestHeaders);

            restTemplate = TestUtil.buildRestTemplate();
            System.out.println("Order URL to call " + ORDER_CREATE );
            ResponseEntity<OrderResponse> entity = restTemplate.exchange(ORDER_CREATE, HttpMethod.POST, requestEntity, OrderResponse.class);

            assertNotNull(entity, "ResponseEntity should not be null");
            assertEquals(entity.getStatusCode(), HttpStatus.BAD_REQUEST, "Status Code not 400");

        } catch(HttpClientErrorException errorException) {
            assertEquals(errorException.getStatusCode(), HttpStatus.BAD_REQUEST, "HttpStatus is not BAD_REQUEST");
            String responseBody = errorException.getResponseBodyAsString();
            OrderResponse response = deserilizeOrderResponse(responseBody);
            String orderId = response.getOrderId();

            //Get Order Details
            ResponseEntity<GetOrderResponse> getOrderResponseEntity = getOrdersFromServer(restTemplate, orderId);
            assertNotNull(getOrderResponseEntity, "Response Entity should not null");
            assertEquals(getOrderResponseEntity.getStatusCode(), HttpStatus.OK,"Status Code not 200");
            assertNotNull(getOrderResponseEntity.getBody(), "Get Order response should not be null");
            GetOrderResponse getOrderResponse = getOrderResponseEntity.getBody();

            assertEquals(getOrderResponse.getPaymentInformation().getStatus(), StatusEnum.PAYMENT_AUTHORIZE_FAILED.name(),
                    "Payment status does not match");
            assertEquals(getOrderResponse.getOrderStatus(), StatusEnum.ORDER_CANCELLED.name(), "Order Status does not match");

        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail("Failed to execute test", e);
        }
    }

    private OrderResponse deserilizeOrderResponse(String responseBody) {
        JsonConverter jsonConverter = new JsonConverterImpl();
        return jsonConverter.deserialize(OrderResponse.class, responseBody);
    }

    private ResponseEntity<PaymentResponse> acceptPayment(RestTemplate restTemplate, String paymentId, String action) {
        return restTemplate.exchange(PAYMENT_URL+paymentId+"/"+action, HttpMethod.PUT, null, PaymentResponse.class);
    }

    private void sleepFor(int sleepFor) {
        try {
            Thread.sleep(sleepFor);
        } catch (Exception e) {
            //Ignore exception
        }
    }

    /**
     * Makes get call to get order details.
     *
     * @param restTemplate
     * @param orderId
     * @return
     */
    private ResponseEntity<GetOrderResponse> getOrdersFromServer(RestTemplate restTemplate, String orderId) {
        return restTemplate.exchange(GET_ORDER+orderId, HttpMethod.GET, null, GetOrderResponse.class);
    }

    /**
     * Makes get call to get payment details.
     *
     * @param restTemplate
     * @param paymentId
     * @return
     */
    private ResponseEntity<PaymentResponse> getPaymentFromServer(RestTemplate restTemplate, String paymentId) {
        return restTemplate.exchange(PAYMENT_URL+paymentId, HttpMethod.GET, null, PaymentResponse.class);
    }
}
