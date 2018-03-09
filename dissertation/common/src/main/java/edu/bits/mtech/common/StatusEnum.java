/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.common;

public enum StatusEnum {

    PAYMENT_AUTHORIZED("PAYMENT_AUTHORIZED"),
    PAYMENT_AUTHORIZE_FAILED("PAYMENT_AUTHORIZE_FAILED"),
    PAYMENT_CAPTURED("PAYMENT_CAPTURED"),
    PAYMENT_FAILED("PAYMENT_FAILED"),
    PAYMENT_CANCELLED("PAYMENT_CANCELLED"),
    PAYMENT_SUCCESSFUL("PAYMENT_SUCCESSFUL"),
    PAYMENT_REJECTED("PAYMENT_REJECTED"),
    ORDER_CANCELLED("ORDER_CANCELLED"),
    ORDER_CREATED("ORDER_CREATED")
    ;

    private String status;
    StatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
