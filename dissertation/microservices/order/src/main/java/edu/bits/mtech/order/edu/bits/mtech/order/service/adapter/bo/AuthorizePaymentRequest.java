/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.edu.bits.mtech.order.service.adapter.bo;

import javax.validation.constraints.NotNull;

/**
 * Business object for payment authorization.
 *
 * @author Tushar Phadke
 */
public class AuthorizePaymentRequest {

    @NotNull(message = "Order Id should not be null")
    private String orderId;
    private String cardNumber;
    private String nameOnCard;
    private String cvv;
    private double authorizeAmount;
    private long customerId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public double getAuthorizeAmount() {
        return authorizeAmount;
    }

    public void setAuthorizeAmount(double authorizeAmount) {
        this.authorizeAmount = authorizeAmount;
    }

    @Override
    public String toString() {
        return "AuthorizePaymentRequest{" +
                "orderId='" + orderId + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", nameOnCard='" + nameOnCard + '\'' +
                ", cvv='" + cvv + '\'' +
                ", authorizeAmount=" + authorizeAmount +
                '}';
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }
}
