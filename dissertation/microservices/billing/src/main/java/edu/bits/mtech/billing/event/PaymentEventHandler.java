/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.event;

import edu.bits.mtech.billing.db.bo.Bill;
import edu.bits.mtech.billing.db.bo.Payment;
import edu.bits.mtech.billing.db.repository.BillRepository;
import edu.bits.mtech.billing.kafka.BillEventProducer;
import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.StatusEnum;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.common.event.EventHandler;
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
public class PaymentEventHandler implements EventHandler {

    private static final Logger logger = Logger.getLogger(PaymentEventHandler.class.getName());

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillEventProducer orderEventProducer;

    @Override
    public void handleEvent(Event event) {

        StatusEnum status = event.getStatus();
        switch (status) {
            case PAYMENT_AUTHORIZED:
                createPayment(event);
                break;
            case PAYMENT_CAPTURED:
                createPayment(event);
                break;
            case PAYMENT_REJECTED:
                updatePaymentStatus(event);
                break;
            case PAYMENT_CANCELLED:
                updatePaymentStatus(event);
                break;
            case PAYMENT_SUCCESSFUL:
                updatePaymentStatus(event);
                break;
            case PAYMENT_AUTHORIZE_FAILED:
                updatePaymentStatus(event);
                break;
            default:
                event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
                billRepository.save(event);
                logger.info("Ignoring event: " + event);
        }
    }

    private void createPayment(Event event) {
        Bill bill = billRepository.findBillByKey(event.getBillId());
        if (bill == null) {
            logger.warning("Billing: Bill does not exits");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            billRepository.save(event);
            return;
        } else if (bill.getPayment() == null) {
            Payment payment = new Payment();
            payment.setPaymentId(event.getPaymentId());
            payment.setAuthorizeAmount(event.getAuthorizeAmount());
            payment.setStatus(event.getStatus().name());
            bill.setPayment(payment);

            try {
                billRepository.save(payment);
                billRepository.update(bill);
                logger.info("Bill: Payment created successfully");
                event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
                billRepository.save(event);
            } catch(Exception e) {
                logger.log(Level.WARNING, "Failed to save bill/order");
                event.setActionTaken(BitsPocConstants.ACTION_PENDING);
                billRepository.save(event);
            }
        } else {

            try {
                bill.getPayment().setStatus(event.getStatus().name());
                billRepository.update(bill.getPayment());
                logger.info("Bill: Payment updated successfully");
                event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
                billRepository.save(event);
            } catch(Exception e) {
                logger.log(Level.WARNING, "Failed to save bill/order");
                event.setActionTaken(BitsPocConstants.ACTION_PENDING);
                billRepository.save(event);
            }
        }
    }

    private void updatePaymentStatus(Event event) {
        Payment payment = billRepository.findPaymentByKey(event.getPaymentId());
        Bill bill = billRepository.findBillByKey(event.getBillId());
        if (bill == null) {
            logger.warning("Billing: Bill does not exits");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            billRepository.save(event);
            return;
        } else if (payment == null) {

            payment = new Payment();
            payment.setPaymentId(event.getPaymentId());
            payment.setStatus(event.getStatus().name());
            payment.setAuthorizeAmount(event.getAuthorizeAmount());
            payment.setBill(bill);
            bill.setPayment(payment);

            try {
                billRepository.save(payment);
                billRepository.update(bill);
                logger.info("Payment created successfully");
                event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
                billRepository.save(event);
            } catch(Exception e) {
                logger.log(Level.WARNING, "Failed to save bill/order");
                event.setActionTaken(BitsPocConstants.ACTION_PENDING);
                billRepository.save(event);
            }
        } else {
            payment.setStatus(event.getStatus().name());
            billRepository.update(payment);

            //Save event
            event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
            billRepository.save(event);
        }
    }
}
