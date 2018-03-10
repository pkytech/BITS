/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.common.bo;

import edu.bits.mtech.common.StatusEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Event which is Posted to queue.
 *
 * @author Tushar Phadke
 */
@Entity
@Table(name = "EVENT")
public class Event {

    private String eventId;
    private String source;
    private String orderId;
    private String paymentId;
    private String actionTaken;
    private StatusEnum status;

    @Id
    @Column(name = "EVENT_ID", length = 50)
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Column(name = "EVENT_SOURCE", length = 50)
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Column(name = "ORDER_ID", length = 50)
    public String getOrderId() {
        return orderId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Column(name = "PAYMENT_ID", length = 50)
    public String getPaymentId() {
        return paymentId;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Column(name = "STATUS", length = 20)
    public StatusEnum getStatus() {
        return status;
    }

    @Column(name = "ACTION_TAKEN", length = 50)
    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", source='" + source + '\'' +
                ", orderId='" + orderId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", actionTaken='" + actionTaken + '\'' +
                ", status=" + status +
                '}';
    }
}
