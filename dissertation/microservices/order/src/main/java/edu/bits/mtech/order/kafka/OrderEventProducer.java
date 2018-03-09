/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.kafka;

import edu.bits.mtech.common.bo.Event;

public interface OrderEventProducer {
    public void produceEvent(Event message);
}
