package edu.bits.mtech.payment.service.bo;

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
    private double auhtorizeAmount;
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

    @Override
    public String toString() {
        return "AuthorizePaymentResponse{" +
                "paymentId='" + paymentId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setAuhtorizeAmount(double auhtorizeAmount) {
        this.auhtorizeAmount = auhtorizeAmount;
    }

    public double getAuhtorizeAmount() {
        return auhtorizeAmount;
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
}
