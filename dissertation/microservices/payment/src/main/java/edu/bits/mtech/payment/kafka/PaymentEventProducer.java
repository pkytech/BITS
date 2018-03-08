package edu.bits.mtech.payment.kafka;

import edu.bits.mtech.common.bo.Event;

public interface PaymentEventProducer {
    public void produceEvent(Event message);
}
