/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.service.bo;

import java.util.List;

/**
 * Business Object for returning Order Information.
 *
 * @author Tushar Phadke
 */
public class GetOrderResponse {
    private PaymentInformation paymentInformation;
    private List<OrderLineItem> items;
    private String billId;
    private long customerId;
    private double orderAmt;
    private String orderId;
    private String orderStatus;

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

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
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

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "GetOrderResponse{" +
                "paymentInformation=" + paymentInformation +
                ", items=" + items +
                ", billId='" + billId + '\'' +
                ", customerId=" + customerId +
                ", orderAmt=" + orderAmt +
                ", orderId='" + orderId + '\'' +
                '}';
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
