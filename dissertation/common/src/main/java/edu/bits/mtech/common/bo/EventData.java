package edu.bits.mtech.common.bo;

import edu.bits.mtech.common.StatusEnum;

public class EventData {
    private String orderId;
    private String paymentId;
    private StatusEnum status;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public StatusEnum getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "orderId='" + orderId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", status=" + status +
                '}';
    }
}
