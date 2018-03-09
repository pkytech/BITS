/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.db.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for Payment
 */
@Entity
@Table(name = "PAYMENT")
public class Payment {

    private String paymentId;
    private String status;
    private double authorizeAmount;
    private String orderId;

    @Id
    @Column(name = "PAYMENT_ID", length = 50)
    public String getPaymentId() {
        return paymentId == null ? "TEMP-"+System.currentTimeMillis() : paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Column(name = "PAYMENT_STATUS", length = 20)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "AUTHORIZE_AMT")
    public double getAuthorizeAmount() {
        return authorizeAmount;
    }

    public void setAuthorizeAmount(double authorizeAmount) {
        this.authorizeAmount = authorizeAmount;
    }

    @Column(name = "ORDER_ID", length = 50)
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", status='" + status + '\'' +
                ", authorizeAmount=" + authorizeAmount +
                '}';
    }
}
