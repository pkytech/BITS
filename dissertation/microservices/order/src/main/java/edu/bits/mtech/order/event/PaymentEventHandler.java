/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.event;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.StatusEnum;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.common.event.EventHandler;
import edu.bits.mtech.order.db.bo.Order;
import edu.bits.mtech.order.db.repository.OrderRepository;
import edu.bits.mtech.order.kafka.OrderEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Order Event Handler.
 *
 * @author Tushar Phadke
 */
@Service
public class PaymentEventHandler implements EventHandler {

    private static final Logger logger = Logger.getLogger(PaymentEventHandler.class.getName());

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventProducer orderEventProducer;

    @Override
    public void handleEvent(Event event) {

        StatusEnum status = event.getStatus();
        switch (status) {
            case PAYMENT_REJECTED:
                handlerPaymentRejectedEvent(event);
                break;
            case PAYMENT_CANCELLED:
                handlePaymentCancelledEvent(event);
                break;
            case PAYMENT_SUCCESSFUL:
                updateOrderStatus(event);
                break;
            case PAYMENT_AUTHORIZE_FAILED:
                handlePaymentAuthorizeFailed(event);
            default:
                event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
                orderRepository.save(event);
                logger.info("Ignoring event: " + event);
        }
    }

    private void handlePaymentAuthorizeFailed(Event event) {
        Order order = orderRepository.findOrderByKey(event.getOrderId());
        if (order == null) {
            logger.info("Payment authroze failed event ignored as order/orderId is null");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            orderRepository.save(event);
            return;
        }

        try {
            order.setStatus(StatusEnum.ORDER_CANCELLED.name());
            order.getPayment().setStatus(StatusEnum.PAYMENT_AUTHORIZE_FAILED.name());
            orderRepository.update(order.getPayment());
            orderRepository.update(order);

            event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
            orderRepository.save(event);
            fireOrderEvent(order.getOrderId(), order.getPayment().getPaymentId(), StatusEnum.ORDER_CONFIRMED);
        } catch(Exception e) {
            logger.log(Level.WARNING, "Failed to handle payment authorize failed order/event and fire event");

            event.setActionTaken(BitsPocConstants.ACTION_PENDING);
            orderRepository.save(event);
        }
    }

    private void updateOrderStatus(Event event) {
        Order order = orderRepository.findOrderByKey(event.getOrderId());
        if (order == null) {
            logger.info("Payment cancelled event ignored as order/orderId is null");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            orderRepository.save(event);
            return;
        }

        try {
            order.setStatus(StatusEnum.ORDER_CONFIRMED.name());
            order.getPayment().setStatus(StatusEnum.PAYMENT_SUCCESSFUL.name());
            orderRepository.update(order.getPayment());
            orderRepository.update(order);

            event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
            orderRepository.save(event);
            fireOrderEvent(order.getOrderId(), order.getPayment().getPaymentId(), StatusEnum.ORDER_CONFIRMED);
        } catch(Exception e) {
            logger.log(Level.WARNING, "Failed to save order/event and fire event");

            event.setActionTaken(BitsPocConstants.ACTION_PENDING);
            orderRepository.save(event);
        }
    }

    private void handlePaymentCancelledEvent(Event event) {
        logger.info("Handle payment cancelled event");
        String orderKey = event.getOrderId();
        if (orderKey == null) {
            logger.info("Payment cancelled event ignored as order id is null");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            orderRepository.save(event);
            return;
        }
        Order order = orderRepository.findOrderByKey(orderKey);
        if (orderKey == null) {
            logger.info("Payment cancelled event ignored as order is null");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            orderRepository.save(event);
            return;
        }
        order.setStatus(StatusEnum.ORDER_CANCELLED.name());
        try {
            orderRepository.update(order);

            event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
            orderRepository.save(order);
        } catch(Exception e) {
            logger.log(Level.WARNING, "Failed to save order");
        }
    }

    private void handlerPaymentRejectedEvent(Event event) {
        logger.info("Handle payment reject event");
        String orderKey = event.getOrderId();
        if (orderKey == null) {
            logger.info("Payment reject event ignored as order id is null");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            orderRepository.save(event);
            return;
        }
        Order order = orderRepository.findOrderByKey(orderKey);
        if (orderKey == null) {
            logger.info("Payment reject event ignored as order is null");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            orderRepository.save(event);
            return;
        }

        try {
            order.setStatus(StatusEnum.ORDER_REJECTED.name());
            order.getPayment().setStatus(StatusEnum.PAYMENT_REJECTED.name());
            orderRepository.update(order.getPayment());
            orderRepository.update(order);


            event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
            orderRepository.save(order);

            fireOrderEvent(order.getOrderId(), order.getPayment().getPaymentId(), StatusEnum.ORDER_REJECTED);
        } catch(Exception e) {
            logger.log(Level.WARNING, "Failed to update order");
        }
    }

    private void fireOrderEvent(String orderId, String paymentId, StatusEnum orderStatus) {
        Event event = new Event();
        event.setEventId(UUID.randomUUID().toString());
        event.setOrderId(orderId);
        event.setSource(BitsPocConstants.ORDER_SERVICE.toUpperCase());
        event.setPaymentId(paymentId);
        event.setStatus(orderStatus);

        orderEventProducer.produceEvent(event);
    }

}
