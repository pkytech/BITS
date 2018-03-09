/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.edu.bits.mtech.order.service.adapter;

import edu.bits.mtech.order.db.bo.Order;
import edu.bits.mtech.order.edu.bits.mtech.order.service.adapter.bo.AuthorizePaymentResponse;
import edu.bits.mtech.order.edu.bits.mtech.order.service.bo.PaymentInformation;

public interface PaymentAdapter {

    AuthorizePaymentResponse authorizePayment(Order order, PaymentInformation paymentInformation);
}
