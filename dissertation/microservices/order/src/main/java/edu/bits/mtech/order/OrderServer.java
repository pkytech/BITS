package edu.bits.mtech.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Customer Microservice which registers with Eureka Server for discovery.
 * 
 * @author Tushar Phadke
 */ 
@EnableAutoConfiguration
@EnableDiscoveryClient
public class OrderServer {

  public static void main(String[] args) {
    // Tell Boot to look for registration-server.yml
    System.setProperty("spring.config.name", "order-server");
    SpringApplication.run(OrderServer.class, args);
  }
}
