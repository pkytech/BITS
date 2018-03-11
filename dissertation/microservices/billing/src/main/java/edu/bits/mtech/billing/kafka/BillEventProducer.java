/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.kafka;

import edu.bits.mtech.common.bo.Event;

public interface BillEventProducer {
    public void produceEvent(Event message);
}
