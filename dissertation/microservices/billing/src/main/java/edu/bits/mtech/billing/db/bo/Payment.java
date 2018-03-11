/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.db.bo;

import javax.persistence.*;
import java.util.UUID;

/**
 * Entity for Payment
 */
@Entity
@Table(name = "PAYMENT")
public class Payment {

    private String paymentId;
    private String status;
    private double authorizeAmount;
    private Bill bill;

    @Id
    @Column(name = "PAYMENT_ID", length = 50)
    public String getPaymentId() {
        return paymentId == null ? UUID.randomUUID().toString() : paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Column(name = "PAYMENT_STATUS", length = 30)
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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
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
