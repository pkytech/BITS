package edu.bits.mtech.payment.kafka;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.JsonConverter;
import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.common.event.EventHandler;
import edu.bits.mtech.payment.db.repository.PaymentRepository;
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

	@Autowired
	private EventHandler orderEventHandler;

	@Autowired
	private PaymentRepository paymentRepository;

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
		if (event == null || event.getSource() == null || BitsPocConstants.PAYMENT_SERVICE.equalsIgnoreCase(event.getSource())) {
			if (event != null) {
				event.setActionTaken(BitsPocConstants.ACTION_IGNORED);
				paymentRepository.save(event);
			}
			return;
		}

		//Order Events
		if (BitsPocConstants.ORDER_SERVICE.equalsIgnoreCase(event.getSource())) {
			orderEventHandler.handleEvent(event);
		}
	}
}
