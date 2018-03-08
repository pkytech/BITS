package edu.bits.mtech.payment.kafka;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bits.mtech.common.JsonConverter;
import edu.bits.mtech.common.bo.Event;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

/**
 * Default message listener.
 *
 * @author Tushar Phadke
 */
@Service
public class KafkaEventListener implements MessageListener<Integer, String> {


	@Autowired
	private JsonConverter jsonConverter;

	private static final Logger logger = Logger.getLogger(KafkaEventListener.class.getName());
	
	@Override
	public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {

		logger.info("Received Kafka Message: 2----> " + consumerRecord);

		if (consumerRecord == null) {
			return;
		}
		String message = consumerRecord.value();
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Message received from Kafka: " + message);
		}

		Event event = null;
		try {
			event = jsonConverter.deserialize(Event.class, message);
		} catch (IllegalStateException ilse) {
			logger.log(Level.WARNING, "Failed to convert message", ilse);
			return;
		}
        logger.info("Received Kafka Message: 3----> " + event);
		if (event == null || event.getSource() == null || "PAYMENT".equalsIgnoreCase(event.getSource())) {
			return;
		}
	}
}
