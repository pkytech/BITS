package edu.bits.mtech.payment.service.bo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;

/**
 * Business object for payment authorization.
 *
 * @author Tushar Phadke
 */
@JsonIgnoreProperties
public class AuthorizePaymentRequest {

    @NotNull(message = "Order Id should not be null")
    private String orderId;
    private String cardNumber;
    private String nameOnCard;
    private String cvv;
    private String billId;
    private double authorizeAmount;
    private double captureAmount;
    private long customerId;
    private String paymentId;

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

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getCaptureAmount() {
        return captureAmount;
    }

    public void setCaptureAmount(double captureAmount) {
        this.captureAmount = captureAmount;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    @Override
    public String toString() {
        return "AuthorizePaymentRequest{" +
                "orderId='" + orderId + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", nameOnCard='" + nameOnCard + '\'' +
                ", cvv='" + cvv + '\'' +
                ", billId='" + billId + '\'' +
                ", authorizeAmount=" + authorizeAmount +
                ", captureAmount=" + captureAmount +
                ", customerId=" + customerId +
                ", paymentId='" + paymentId + '\'' +
                '}';
    }
}
