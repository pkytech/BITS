/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.bo;

/**
 * Business object for OrderResponse.
 *
 * @author Tushar Phadke
 */
public class OrderResponse {

    private String orderId;
    private String paymentId;
    private String paymentStatus;
    private String orderStatus;
    private String status;
    private String billId;
    private String errorMessage;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orderId='" + orderId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", status='" + status + '\'' +
                ", billId='" + billId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
