package edu.bits.mtech.customer;

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
public class CustomerServer {

  public static void main(String[] args) {
    // Tell Boot to look for registration-server.yml
    System.setProperty("spring.config.name", "customer-server");
    SpringApplication.run(CustomerServer.class, args);
  }
}
