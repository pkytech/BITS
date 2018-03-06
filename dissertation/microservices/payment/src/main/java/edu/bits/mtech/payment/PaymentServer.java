package edu.bits.mtech.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Payment Microservice which registers with Eureka Server for discovery.
 * 
 * @author Tushar Phadke
 */ 
@EnableAutoConfiguration
@EnableDiscoveryClient
public class PaymentServer {

  public static void main(String[] args) {
    // Tell Boot to look for registration-server.yml
    System.setProperty("spring.config.name", "payment-server");
    SpringApplication.run(PaymentServer.class, args);
  }
}
