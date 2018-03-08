package edu.bits.mtech.payment.kafka;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.JsonConverter;
import edu.bits.mtech.common.bo.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class PaymentEventProducerImpl implements PaymentEventProducer {

    private static final Logger logger = Logger.getLogger(PaymentEventProducerImpl.class.getName());

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
