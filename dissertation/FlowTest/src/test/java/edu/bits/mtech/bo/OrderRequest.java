/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * Business object for creating order.
 *
 * @author Tushar Phadke
 */
public class OrderRequest {

    private PaymentInformation paymentInformation;
    private List<OrderLineItem> items = new ArrayList<OrderLineItem>();
    private String billId;
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

    @Override
    public String toString() {
        return "OrderRequest{" +
                "paymentInformation=" + paymentInformation +
                ", items=" + items +
                ", billId='" + billId + '\'' +
                ", customerId=" + customerId +
                ", orderAmt=" + orderAmt +
                '}';
    }

    /**
     * Creates one order line item and adds it to list.
     *
     * @param productName
     * @param quantity
     * @param price
     * @param cost
     */
    public void addOrderLineItem(String productName, double quantity,double price, double cost) {
        OrderLineItem item = new OrderLineItem();
        item.setProductName(productName);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setCost(cost);
        items.add(item);
    }
}
