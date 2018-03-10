/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.common;

/**
 * Constants.
 *
 * @author Tushar Phadke
 */
public interface BitsPocConstants {

    String PAYMENT_SERVICE = "payment-service";
    String ACQUIRER_SERVICE = "acquirer-service";
    String BILLING_SERVICE = "billing-service";
    String ORDER_SERVICE = "order-service";

    String PAYMENT_SERVER_NAME = "payment-server";
    String ACQUIRER_SERVER_NAME = "acquirer-server";
    String BILLING_SERVER_NAME = "billing-server";
    String ORDER_SERVER_NAME = "order-server";
    String KAFKA_QUEUE_NAME = "test";

    String ACTION_IGNORED = "IGNORED";
    String ACTION_PENDING = "PENDING";
    String ACTION_COMPLETED = "COMPLETED";
}
