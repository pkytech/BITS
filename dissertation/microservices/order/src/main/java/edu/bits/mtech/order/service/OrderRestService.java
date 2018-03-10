/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.service;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.StatusEnum;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.order.db.bo.Order;
import edu.bits.mtech.order.db.bo.Payment;
import edu.bits.mtech.order.db.repository.OrderRepository;
import edu.bits.mtech.order.service.adapter.PaymentAdapter;
import edu.bits.mtech.order.service.adapter.bo.AuthorizePaymentResponse;
import edu.bits.mtech.order.service.bo.*;
import edu.bits.mtech.order.kafka.OrderEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * REST Service for order management.
 *
 * @author Tushar Phadke
 */
@RestController
public class OrderRestService {

    private static final Logger logger = Logger.getLogger(OrderRestService.class.getName());

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentAdapter paymentAdapter;

    @Autowired
    private OrderEventProducer orderEventProducer;

    @RequestMapping(value = "/rest/order/{orderId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetOrderResponse> getOrder(@PathVariable("orderId") String orderId) {

        Order order = orderRepository.findOrderByKey(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        GetOrderResponse response = populateGetOrder(order);
        return ResponseEntity.ok(response);
    }

    private GetOrderResponse populateGetOrder(Order order) {
        GetOrderResponse response = new GetOrderResponse();
        response.setCustomerId(order.getCustomerId());
        response.setOrderAmt(order.getOrderAmount());
        response.setOrderId(order.getOrderId());
        response.setOrderStatus(order.getStatus());

        PaymentInformation paymentInformation = new PaymentInformation();
        paymentInformation.setStatus(order.getPayment().getStatus());
        paymentInformation.setPaymentId(order.getPayment().getAcquirerPaymentId());
        paymentInformation.setPaymentAmt(order.getPayment().getAuthorizeAmount());
        response.setPaymentInformation(paymentInformation);

        return response;
    }


    @RequestMapping(value = "/rest/order", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> createOrder(@RequestBody
                            @NotNull(message = "Order should not be null") @Valid @Validated OrderRequest orderRequest) {

        logger.info("OrderCreate [POST]: " + orderRequest);
        OrderResponse orderResponse = new OrderResponse();
        Order order = populateOrderEntity(orderRequest);

        try {

            orderRepository.save(order);
            logger.info("Order Saved: Order[" + order+"], Payment ["+order.getPayment() + "], Bill ["+order.getBill() + "]");

            AuthorizePaymentResponse response = paymentAdapter.authorizePayment(order, orderRequest.getPaymentInformation());
            if (response == null || response.getPaymentStatusCode() != StatusEnum.PAYMENT_CAPTURED) {
                logger.info("Payment Authorize failed");

                order.setStatus(StatusEnum.ORDER_CANCELLED.name());
                orderRepository.update(order);

                fireOrderEvent(order.getOrderId(), null, StatusEnum.ORDER_CANCELLED);
            } else {

                Payment payment = order.getPayment();
                payment.setAcquirerPaymentId(response.getPaymentId());
                payment.setStatus(response.getPaymentStatusCode().name());
                payment.setAuthorizeAmount(response.getAuthorizeAmount());

                logger.info("Updating Payment: " + payment);
                orderRepository.update(payment);

                //update order with accepted status
                order.setStatus(StatusEnum.ORDER_CONFIRMED.name());
                logger.info("Updating Order: " + payment);
                orderRepository.update(order);

                fireOrderEvent(order.getOrderId(), response.getPaymentId(), StatusEnum.ORDER_CONFIRMED);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to create order", e);

            //Cancel order and
            order.setStatus(StatusEnum.ORDER_CANCELLED.name());
            order.getPayment().setStatus(StatusEnum.PAYMENT_CANCELLED.name());
            orderRepository.update(order);

            fireOrderEvent(order.getOrderId(), order.getPayment().getAcquirerPaymentId(), StatusEnum.ORDER_CANCELLED);
        }
        orderResponse.setOrderId(order.getOrderId());
        orderResponse.setOrderStatus(order.getStatus());
        orderResponse.setPaymentId(order.getPayment().getAcquirerPaymentId());
        orderResponse.setPaymentStatus(order.getPayment().getStatus());

        return ResponseEntity.accepted().body(orderResponse);
    }

    private void fireOrderEvent(String orderId, String paymentId, StatusEnum orderStatus) {
        Event event = new Event();
        event.setEventId(UUID.randomUUID().toString());
        event.setSource(BitsPocConstants.ORDER_SERVICE.toUpperCase());
        event.setOrderId(orderId);
        event.setPaymentId(paymentId);
        event.setStatus(orderStatus);
        orderEventProducer.produceEvent(event);
    }

    private Order populateOrderEntity(OrderRequest orderRequest) {
        Order order = new Order();
        if (order == null) {
            return null;
        }
        order.setOrderId(UUID.randomUUID().toString());
        order.setPayment(populatePayment(orderRequest, order.getOrderId()));
        order.setItems(populateOrderLineItems(order, orderRequest.getItems()));
        order.setOrderAmount(orderRequest.getOrderAmt());
        order.setStatus(StatusEnum.ORDER_CREATED.name());
        return order;
    }

    private List<edu.bits.mtech.order.db.bo.OrderLineItem> populateOrderLineItems(Order order, List<OrderLineItem> items) {
        return items.stream().map(item -> lineItem(order, item)).collect(Collectors.toList());
    }

    private edu.bits.mtech.order.db.bo.OrderLineItem lineItem(Order order, OrderLineItem orderLineItem) {
        edu.bits.mtech.order.db.bo.OrderLineItem item = new edu.bits.mtech.order.db.bo.OrderLineItem();
        item.setCost(orderLineItem.getCost());
        item.setOrder(order);
        item.setOrderLineItemId(UUID.randomUUID().toString());
        item.setPrice(orderLineItem.getPrice());
        item.setProductName(orderLineItem.getProductName());
        item.setQuantity(orderLineItem.getQuantity());
        return item;
    }

    private Payment populatePayment(OrderRequest orderRequest, String orderId) {
        Payment paymentInformation = new Payment();

        paymentInformation.setAuthorizeAmount(orderRequest.getOrderAmt());
        paymentInformation.setOrderId(orderId);
        paymentInformation.setStatus(StatusEnum.PAYMENT_INITIALIZED.name());

        return paymentInformation;
    }

}
