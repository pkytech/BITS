/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.db.bo;

import javax.persistence.*;
import java.util.UUID;

/**
 * Bill entity.
 *
 * @author Tushar Phadke
 */
@Entity
@Table(name = "CUST_BILL")
public class Bill {
    private String billId;
    private double billAmount;
    private Order order;
    private Payment payment;

    @Id
    @Column(name = "BILL_ID", length = 50)
    public String getBillId() {
        return billId == null ? UUID.randomUUID().toString() : billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    @Column(name = "BILL_AMOUNT")
    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", billAmount=" + billAmount +
                ", order=" + order +
                ", payment=" + payment +
                '}';
    }
}
