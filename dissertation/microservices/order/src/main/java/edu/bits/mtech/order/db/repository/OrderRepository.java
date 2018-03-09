/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.db.repository;

import edu.bits.mtech.order.db.bo.Bill;
import edu.bits.mtech.order.db.bo.Order;
import edu.bits.mtech.order.db.bo.Payment;

public interface OrderRepository {

    void save(Payment payment);
    void save(Order order);
    void save(Bill bill);

    Payment findPaymentByKey(String key);

    Order findOrderByKey(String key);

    void updateOrder(Order order);

    void updatePayment(Payment payment);
}
