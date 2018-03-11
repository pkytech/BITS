/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.payment.db.bo;


import javax.persistence.*;

/**
 * Business Object for Payment
 */
@Entity
@Table(name = "PAYMENT")
public class Payment {


    private String paymentId;

    private Order order;

    private long customerId;

    private String billNumber;

    private String cardNumber;

    private String nameOnCard;

    private String cvv;

    private double authorizeAmount;

    private double paymentAmount;

    private String status;

    @Id
    @Column(name = "PAYMENT_ID")
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ORDER_ID")
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "CUSTOMER_ID")
    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    @Column(name = "BILL_ID", length = 50)
    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    @Column(name = "CARD_NUMBER", length = 16)
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Column(name = "NAME_ON_CARD", length = 50)
    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    @Column(name = "CVV", length = 3)
    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Column(name = "AUTHORIZE_AMT")
    public double getAuthorizeAmount() {
        return authorizeAmount;
    }

    public void setAuthorizeAmount(double authorizeAmount) {
        this.authorizeAmount = authorizeAmount;
    }

    @Column(name = "PAYMENT_AMT")
    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
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
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", order=" + order +
                ", customerId=" + customerId +
                ", billNumber='" + billNumber + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", nameOnCard='" + nameOnCard + '\'' +
                ", cvv='" + cvv + '\'' +
                ", authorizeAmount=" + authorizeAmount +
                ", paymentAmount=" + paymentAmount +
                ", status='" + status + '\'' +
                '}';
    }
}
