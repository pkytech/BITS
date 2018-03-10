/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.event;

import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.common.event.EventHandler;
import edu.bits.mtech.order.db.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Billing Event Handler.
 *
 * @author Tushar Phadke
 */
@Service
public class BillingEventHandler implements EventHandler {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void handleEvent(Event event) {

    }
}
