/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.service.adapter;

import edu.bits.mtech.order.service.bo.BillRequest;

/**
 * Adapter for calling billing service.
 *
 * @author
 */
public interface BillAdapter {

    boolean createBill(BillRequest billRequest);
}
