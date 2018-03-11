/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.db.bo;

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
    private Bill bill;
    private double orderAmount;
    private long customerId;
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
                ", orderAmount=" + orderAmount +
                ", customerId=" + customerId +
                ", status='" + status + '\'' +
                '}';
    }
}
