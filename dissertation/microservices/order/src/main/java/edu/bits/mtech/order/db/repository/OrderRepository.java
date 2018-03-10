/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.db.repository;

import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.order.db.bo.Bill;
import edu.bits.mtech.order.db.bo.Order;
import edu.bits.mtech.order.db.bo.Payment;

public interface OrderRepository {

    void save(Object payment);
    void update(Object object);
    Payment findPaymentByKey(String key);
    Order findOrderByKey(String key);
    Event findEventById(String key);
}
