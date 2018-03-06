package edu.bits.mtech.order.kafka;

import java.util.logging.Logger;

import org.springframework.kafka.annotation.KafkaListener;

public class KafkaEventListener {

	private static final Logger logger = Logger.getLogger(KafkaEventListener.class.getName());
	
	@KafkaListener(id = "order-listener", topics = "testnew", clientIdPrefix = "${spring.application.name}")
	public void listen(String data) {
		logger.info("Received Kafka Message: " + data);
	}
}
