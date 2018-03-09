/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.edu.bits.mtech.order.service;

import edu.bits.mtech.common.StatusEnum;
import edu.bits.mtech.order.db.bo.Order;
import edu.bits.mtech.order.db.bo.Payment;
import edu.bits.mtech.order.db.repository.OrderRepository;
import edu.bits.mtech.order.edu.bits.mtech.order.service.bo.OrderLineItem;
import edu.bits.mtech.order.edu.bits.mtech.order.service.bo.OrderRequest;
import edu.bits.mtech.order.edu.bits.mtech.order.service.bo.OrderResponse;
import edu.bits.mtech.order.edu.bits.mtech.order.service.bo.PaymentInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
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

    @RequestMapping(value = "/rest/order", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> createOrder(@RequestBody
                            @NotNull(message = "Order should not be null") @Valid @Validated OrderRequest orderRequest) {

        logger.info("OrderCreate [POST]: " + orderRequest);
        OrderResponse orderResponse = new OrderResponse();
        Order order = populateOrderEntity(orderRequest);

        orderRepository.save(order.getPayment());
        orderRepository.save(order);
        logger.info("Order Saved: " + order);

        orderResponse.setOrderId(order.getOrderId());
        orderResponse.setOrderStatus(order.getStatus());
        orderResponse.setPaymentId(order.getPayment().getPaymentId());
        orderResponse.setPaymentStatus(order.getPayment().getStatus());

        return ResponseEntity.accepted().body(orderResponse);
    }

    private Order populateOrderEntity(OrderRequest orderRequest) {
        Order order = new Order();
        if (order == null) {
            return null;
        }
        order.setOrderId(UUID.randomUUID().toString());
        order.setPayment(populatePayment(orderRequest, order.getOrderId()));
        order.setItems(populateOrderLineItems(order, orderRequest.getItems()));
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
