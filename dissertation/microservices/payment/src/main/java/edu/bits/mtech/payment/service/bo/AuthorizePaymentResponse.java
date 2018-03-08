package edu.bits.mtech.payment.service.bo;

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
}
