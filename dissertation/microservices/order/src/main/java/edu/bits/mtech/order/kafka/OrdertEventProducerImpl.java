/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.kafka;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.JsonConverter;
import edu.bits.mtech.common.bo.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class OrdertEventProducerImpl implements OrderEventProducer {

    private static final Logger logger = Logger.getLogger(OrdertEventProducerImpl.class.getName());

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private JsonConverter jsonConverter;

    @Override
    public void produceEvent(Event message) {
        String messageStr = jsonConverter.serialize(message);
        logger.info("Message posted on queue" + messageStr);
        kafkaTemplate.send(BitsPocConstants.KAFKA_QUEUE_NAME, messageStr);
    }
}
