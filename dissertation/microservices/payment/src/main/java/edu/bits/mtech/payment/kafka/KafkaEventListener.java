package edu.bits.mtech.payment.kafka;

import java.util.logging.Logger;

import org.springframework.kafka.annotation.KafkaListener;

public class KafkaEventListener {

	private static final Logger logger = Logger.getLogger(KafkaEventListener.class.getName());
	
	@KafkaListener(id = "payment-listener", topics = "testnew", clientIdPrefix = "${spring.application.name}")
	public void listen(String data) {
		logger.info("Received Kafka Message: " + data);
	}
}
