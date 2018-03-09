/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.db.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Entity for Payment
 */
@Entity
@Table(name = "PAYMENT")
public class Payment {

    private String paymentId;
    private String acquirerPaymentId;
    private String status;
    private double authorizeAmount;
    private String orderId;

    @Id
    @Column(name = "PAYMENT_ID", length = 50)
    public String getPaymentId() {
        return paymentId == null ? UUID.randomUUID().toString() : paymentId;
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

    @Column(name = "ACQUIRER_PAYMENT_ID", length = 50)
    public String getAcquirerPaymentId() {
        return acquirerPaymentId;
    }

    public void setAcquirerPaymentId(String acquirerPaymentId) {
        this.acquirerPaymentId = acquirerPaymentId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", acquirerPaymentId='" + acquirerPaymentId + '\'' +
                ", status='" + status + '\'' +
                ", authorizeAmount=" + authorizeAmount +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
