/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.edu.bits.mtech.order.service.bo;

/**
 * Business object for payment information.
 *
 * @author Tushar Phadke
 */
public class PaymentInformation {

    private String cardNumber;
    private String cvv;
    private String nameOnCard;
    private double paymentAmt;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public double getPaymentAmt() {
        return paymentAmt;
    }

    public void setPaymentAmt(double paymentAmt) {
        this.paymentAmt = paymentAmt;
    }

    @Override
    public String toString() {
        return "PaymentInformation{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cvv='" + cvv + '\'' +
                ", nameOnCard='" + nameOnCard + '\'' +
                ", paymentAmt=" + paymentAmt +
                '}';
    }
}
