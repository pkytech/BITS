/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.config;

import java.util.HashMap;
import java.util.Map;

import edu.bits.mtech.billing.kafka.KafkaEventListener;
import edu.bits.mtech.common.BitsConfigurator;
import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.common.JsonConverter;
import edu.bits.mtech.common.JsonConverterImpl;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.web.client.RestTemplate;

/**
 * COnfigurator for billing system.
 *
 * @author Tushar Phadke
 */
@Configuration
@EnableKafka
@ComponentScan("edu.bits.mtech.common")
public class BillingConfigurator {

	@Bean
	@Primary
	public JsonConverter buildJsonConverter() {
		return new JsonConverterImpl();
	}

	@Bean
	@LoadBalanced
	public RestTemplate buildRestTemplate() {
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		return restTemplate;
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		int timeout = BitsConfigurator.getIntProperty("bits.mtech.connectionTimeout", 5000);
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.setSocketTimeout(timeout)
				.build();
		CloseableHttpClient client = HttpClientBuilder
				.create()
				.setDefaultRequestConfig(config)
				.build();
		return new HttpComponentsClientHttpRequestFactory(client);
	}

	@Autowired
	private AutowireCapableBeanFactory autowireBeanFactory;

	@Bean
	KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
	kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.getContainerProperties().setPollTimeout(BitsConfigurator.getIntProperty(
				"bits.mtech.kafka.poll-timeout", 3000));
		factory.setConcurrency(3);

		return factory;
	}

	@Bean
	public ConsumerFactory<Integer, String> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerProps());
	}

	@Bean
	public KafkaTemplate<Integer, String> kafkaTemplate() {
		ProducerFactory<Integer, String> pf = new DefaultKafkaProducerFactory<Integer, String>(producerFactory());
		KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);
		return template;
	}

	@Bean
	public KafkaMessageListenerContainer<Integer, String> createContainer() {
		Map<String, Object> props = consumerProps();
		KafkaEventListener kafkaEventListener = new KafkaEventListener();
		autowireBeanFactory.autowireBean(kafkaEventListener);
		ContainerProperties containerProps = new ContainerProperties(BitsPocConstants.KAFKA_QUEUE_NAME);
		containerProps.setMessageListener(kafkaEventListener);

		DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<Integer, String>(props);
		return new KafkaMessageListenerContainer<>(cf,
				containerProps);
	}

	private Map<String, Object> producerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BitsConfigurator.getProperty("bits.mtech.kafka.server",
				"localhost:9092"));
		props.put(ConsumerConfig.GROUP_ID_CONFIG, BitsPocConstants.BILLING_SERVICE);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, BitsConfigurator.getProperty(
				"bits.mtech.kafka.autocommit-interval", "100"));
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, BitsConfigurator.getProperty(
				"bits.mtech.kafka.session-timeout", "15000"));
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}

	private Map<String, Object> consumerProps() {

		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BitsConfigurator.getProperty("bits.mtech.kafka.server",
				"localhost:9092"));
		props.put(ConsumerConfig.GROUP_ID_CONFIG, BitsPocConstants.BILLING_SERVICE);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, BitsConfigurator.getProperty(
				"bits.mtech.kafka.autocommit-interval", "100"));
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, BitsConfigurator.getProperty(
				"bits.mtech.kafka.session-timeout", "15000"));
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return props;
	}
}
