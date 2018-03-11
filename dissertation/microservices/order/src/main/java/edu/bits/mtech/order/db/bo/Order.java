/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.db.bo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity for Order.
 *
 * @author Tushar Phadke
 */
@Entity
@Table(name = "CUST_ORDER")
public class Order {

    private String orderId;
    private Payment payment;
    private Bill bill;
    private double orderAmount;
    private long customerId;
    private List<OrderLineItem> items = new ArrayList<>();
    private String status;

    @Id
    @Column(name = "ORDER_ID", length = 50)
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    @Column(name = "ORDER_AMT")
    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    @Column(name = "CUSTOMER_ID")
    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<OrderLineItem> getItems() {
        return items;
    }

    public void setItems(List<OrderLineItem> items) {
        this.items = items;
    }

    @Column(name = "STATUS", length = 30)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", payment=" + payment +
                ", bill=" + bill +
                ", orderAmount=" + orderAmount +
                ", customerId=" + customerId +
                ", items=" + items +
                ", status='" + status + '\'' +
                '}';
    }
}
