/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.event;

import edu.bits.mtech.billing.db.bo.Bill;
import edu.bits.mtech.billing.db.bo.Order;
import edu.bits.mtech.billing.db.repository.BillRepository;
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
public class OrderEventHandler implements EventHandler {

    private static final Logger logger = Logger.getLogger(OrderEventHandler.class.getName());

    @Autowired
    private BillRepository billRepository;


    @Override
    public void handleEvent(Event event) {

        StatusEnum status = event.getStatus();
        switch (status) {
            case ORDER_CREATED:
                createOrder(event);
                break;
            case ORDER_CANCELLED:
                updateOrderStatus(event);
                break;
            case ORDER_REJECTED:
                updateOrderStatus(event);
                break;
            case ORDER_CONFIRMED:
                updateOrderStatus(event);
            default:
                event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
                billRepository.save(event);
                logger.info("Ignoring event: " + event);
        }
    }

    private void createOrder(Event event) {
        String billId = event.getBillId();
        Bill bill = billRepository.findBillByKey(billId);
        if (bill == null) {
            logger.warning("Billing: bill does not exits");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            billRepository.save(event);
            return;
        } else {
            Order order = new Order();
            order.setOrderId(event.getOrderId());
            order.setStatus(event.getStatus().name());
            order.setBill(bill);
            bill.setOrder(order);

            try {
                billRepository.save(order);

                billRepository.update(order);
                event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
                billRepository.save(event);
            } catch(Exception e) {
                logger.log(Level.WARNING, "Failed to save bill/order");
                event.setActionTaken(BitsPocConstants.ACTION_PENDING);
                billRepository.save(event);
            }
        }
    }

    private void updateOrderStatus(Event event) {
        String orderId = event.getOrderId();
        Order order = billRepository.findOrderByKey(event.getOrderId());
        if (order == null) {
            logger.warning("Billing: Order does not exits");
            event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
            billRepository.save(event);
            return;
        } else {
            order.setStatus(event.getStatus().name());
            billRepository.update(order);

            //Save event
            event.setActionTaken(BitsPocConstants.ACTION_COMPLETED);
            billRepository.save(event);
        }
    }
}
