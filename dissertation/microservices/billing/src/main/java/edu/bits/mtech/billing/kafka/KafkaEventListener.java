package edu.bits.mtech.billing.kafka;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.bits.mtech.billing.db.repository.BillRepository;
import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.JsonConverter;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.common.event.EventHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.listener.MessageListener;

public class KafkaEventListener implements MessageListener<Integer, String> {

	@Autowired
	private JsonConverter jsonConverter;

	@Autowired
	@Qualifier("paymentEventHandler")
	private EventHandler paymentEventHandler;

	@Autowired
	@Qualifier("orderEventHandler")
	private EventHandler orderEventHandler;

	@Autowired
	private BillRepository billRepository;

	private static final Logger logger = Logger.getLogger(KafkaEventListener.class.getName());

	@Override
	public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {

		logger.info("Received Kafka Message: " + consumerRecord);

		if (consumerRecord == null) {
			return;
		}
		String message = consumerRecord.value();
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Message received from Kafka to billing service: " + message);
		}

		Event event = null;
		try {
			event = jsonConverter.deserialize(Event.class, message);
		} catch (IllegalStateException ilse) {
			logger.log(Level.WARNING, "Failed to convert message", ilse);
			return;
		}
		if (event == null || event.getSource() == null || BitsPocConstants.BILLING_SERVICE.equalsIgnoreCase(event.getSource())) {

			return;
		}

		//Avoid double handling of event
		Event order = billRepository.findEventById(event.getEventId());
		if (order == null || !BitsPocConstants.ACTION_COMPLETED.equalsIgnoreCase(order.getActionTaken())) {
			if (BitsPocConstants.PAYMENT_SERVICE.equalsIgnoreCase(event.getSource())) {
				paymentEventHandler.handleEvent(event);
			} else if (BitsPocConstants.ORDER_SERVICE.equalsIgnoreCase(event.getSource())) {
				orderEventHandler.handleEvent(event);
			}
		}
	}
}
