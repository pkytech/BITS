/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.payment.event;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.StatusEnum;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.common.event.EventHandler;
import edu.bits.mtech.payment.db.bo.Order;
import edu.bits.mtech.payment.db.bo.Payment;
import edu.bits.mtech.payment.db.repository.PaymentRepository;
import edu.bits.mtech.payment.service.adapter.AcquirerServiceAdapter;
import edu.bits.mtech.payment.service.bo.AcquirerAuthorizeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Order Event Handler.
 *
 * @author Tushar Phadke
 */
@Service
public class OrderEventHandler implements EventHandler {

    private static final Logger logger = Logger.getLogger(OrderEventHandler.class.getName());

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AcquirerServiceAdapter acquirerServiceAdapter;

    @Override
    public void handleEvent(Event event) {

        StatusEnum status = event.getStatus();
        switch (status) {
            case ORDER_CANCELLED:
                handlerOrderRejectedAndCancelledEvent(event);
                break;
            case ORDER_REJECTED:
                handlerOrderRejectedAndCancelledEvent(event);
                break;
            case ORDER_CONFIRMED:
                updateOrderStatus(event);
                break;
            default:
                event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
                paymentRepository.save(event);
                logger.info("Ignoring event: " + event);
        }
    }

    private void updateOrderStatus(Event event) {
        String orderId = event.getOrderId();
        Order order = paymentRepository.findOrderByKey(event.getOrderId());
        if (order == null) {
            logger.warning("Order does not exits");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            paymentRepository.save(event);
            return;
        } else {
            order.setStatus(event.getStatus().name());
            paymentRepository.update(order);

            //Save event
            event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
            paymentRepository.save(event);
        }
    }

    private void handlerOrderRejectedAndCancelledEvent(Event event) {
        Order order = paymentRepository.findOrderByKey(event.getOrderId());
        logger.warning("Order fetched from DB ");
        if (order == null) {
            logger.warning("Order does not found");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            paymentRepository.save(event);
            return;
        }

        String paymentId = order.getPaymentId();
        Payment payment = paymentRepository.findPaymentByKey(paymentId);
        logger.warning("Payment fetched from DB ["+paymentId+"]");
        if (payment == null) {
            logger.warning("Payment does not found");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            paymentRepository.save(event);
            return;
        }

        //Cancel Authorize
        if (payment.getStatus().equalsIgnoreCase(StatusEnum.PAYMENT_AUTHORIZED.name()) ||
                payment.getStatus().equalsIgnoreCase(StatusEnum.PAYMENT_CAPTURED.name())) {

            boolean cancelSuccessful = false;
            try {
                AcquirerAuthorizeResponse acquirerAuthorizeResponse = acquirerServiceAdapter.cancelAuthorize(paymentId);
                cancelSuccessful = true;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to cancel authorize", e);

            }

            //Save event
            event.setActionTaken(cancelSuccessful ? BitsPocConstants.ACTION_COMPLETED : BitsPocConstants.ACTION_PENDING);
            paymentRepository.save(event);
        } else {
            payment.getOrder().setStatus(event.getStatus().name());

            paymentRepository.update(payment.getOrder());

            event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
            paymentRepository.save(event);
        }
    }
}
