/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.payment.db.repository;

import edu.bits.mtech.payment.db.bo.Order;
import edu.bits.mtech.payment.db.bo.Payment;

public interface PaymentRepository {

    void save(Payment payment);

    Payment findPaymentByKey(String key);

    Order findOrderByKey(String key);
}
