/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.service.bo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Business object for payment information.
 *
 * @author Tushar Phadke
 */
@JsonIgnoreProperties
public class PaymentInformation {

    private double paymentAmt;
    private String paymentId;
    private String status;

    public double getPaymentAmt() {
        return paymentAmt;
    }

    public void setPaymentAmt(double paymentAmt) {
        this.paymentAmt = paymentAmt;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "PaymentInformation{" +
                ", paymentAmt=" + paymentAmt +
                ", paymentId='" + paymentId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
