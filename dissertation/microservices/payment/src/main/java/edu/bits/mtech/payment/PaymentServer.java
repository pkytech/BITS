package edu.bits.mtech.payment;

import edu.bits.mtech.common.BitsPocConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Payment Microservice which registers with Eureka Server for discovery.
 * 
 * @author Tushar Phadke
 */ 
@EnableAutoConfiguration
@EnableDiscoveryClient
@ComponentScan(basePackages = "edu.bits.mtech.payment")
public class PaymentServer {

  public static void main(String[] args) {
    // Tell Boot to look for registration-server.yml
    System.setProperty("spring.config.name", BitsPocConstants.PAYMENT_SERVER_NAME);
    SpringApplication.run(PaymentServer.class, args);
  }
}
