/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.service.bo;

import java.util.List;

/**
 * Business object for creating order.
 *
 * @author Tushar Phadke
 */
public class OrderRequest {

    private PaymentInformation paymentInformation;
    private List<OrderLineItem> items;
    private long customerId;
    private double orderAmt;

    public PaymentInformation getPaymentInformation() {
        return paymentInformation;
    }

    public void setPaymentInformation(PaymentInformation paymentInformation) {
        this.paymentInformation = paymentInformation;
    }

    public List<OrderLineItem> getItems() {
        return items;
    }

    public void setItems(List<OrderLineItem> items) {
        this.items = items;
    }


    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public double getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(double orderAmt) {
        this.orderAmt = orderAmt;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "paymentInformation=" + paymentInformation +
                ", items=" + items +
                ", customerId=" + customerId +
                ", orderAmt=" + orderAmt +
                '}';
    }
}
