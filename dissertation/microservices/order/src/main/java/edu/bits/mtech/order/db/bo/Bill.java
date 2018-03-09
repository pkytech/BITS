/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.db.bo;

import javax.persistence.*;

@Entity
@Table(name = "BILL")
public class Bill {
    private String billId;
    private Order order;
    private String status;

    @Id
    @Column(name = "BILL_ID", length = 50)
    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    @OneToOne(fetch = FetchType.EAGER)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "STATUS", length = 20)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", order=" + order +
                ", status='" + status + '\'' +
                '}';
    }
}
