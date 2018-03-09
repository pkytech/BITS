/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.edu.bits.mtech.order.service.adapter.bo;

import edu.bits.mtech.common.StatusEnum;
import org.springframework.http.HttpStatus;

/**
 * Business object for authorize payment response.
 *
 * @author Tushar Phadke
 */
public class AuthorizePaymentResponse {

    private String paymentId;
    private String message;
    private HttpStatus statusCode;
    private double authorizeAmount;
    private String orderId;
    private StatusEnum paymentStatusCode;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setAuthorizeAmount(double authorizeAmount) {
        this.authorizeAmount = authorizeAmount;
    }

    public double getAuthorizeAmount() {
        return authorizeAmount;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setPaymentStatusCode(StatusEnum paymentStatusCode) {
        this.paymentStatusCode = paymentStatusCode;
    }

    public StatusEnum getPaymentStatusCode() {
        return paymentStatusCode;
    }

    @Override
    public String toString() {
        return "AuthorizePaymentResponse{" +
                "paymentId='" + paymentId + '\'' +
                ", message='" + message + '\'' +
                ", statusCode=" + statusCode +
                ", authorizeAmount=" + authorizeAmount +
                ", orderId='" + orderId + '\'' +
                ", paymentStatusCode=" + paymentStatusCode +
                '}';
    }
}
