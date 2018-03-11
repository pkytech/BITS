/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.db.repository;

import edu.bits.mtech.billing.db.bo.Bill;
import edu.bits.mtech.billing.db.bo.Order;
import edu.bits.mtech.billing.db.bo.Payment;
import edu.bits.mtech.common.bo.Event;


/**
 * Repository for bills.
 *
 * @author Tushar Phadke
 */
public interface BillRepository {

    void save(Object payment);
    void update(Object object);
    Bill findBillByKey(String key);
    Payment findPaymentByKey(String key);
    Order findOrderByKey(String key);
    Event findEventById(String key);
}
