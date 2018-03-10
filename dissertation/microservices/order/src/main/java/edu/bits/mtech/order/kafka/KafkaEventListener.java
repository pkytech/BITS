package edu.bits.mtech.order.kafka;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.JsonConverter;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.common.event.EventHandler;
import edu.bits.mtech.order.db.repository.OrderRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;

public class KafkaEventListener implements MessageListener<Integer, String> {

	@Autowired
	private JsonConverter jsonConverter;

	@Autowired
	@Qualifier("paymentEventHandler")
	private EventHandler paymentEventHandler;

	@Autowired
	private OrderRepository orderRepository;

	private static final Logger logger = Logger.getLogger(KafkaEventListener.class.getName());

	@Override
	public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {

		logger.info("Received Kafka Message: " + consumerRecord);

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
		if (event == null || event.getSource() == null || BitsPocConstants.ORDER_SERVICE.equalsIgnoreCase(event.getSource())) {

			return;
		}

		//Avoid double handling of event
		Event order = orderRepository.findEventById(event.getEventId());
		if (order == null || !BitsPocConstants.ACTION_COMPLETED.equalsIgnoreCase(order.getActionTaken())) {
			paymentEventHandler.handleEvent(event);
		}
	}
}
