package edu.bits.mtech.order;

import edu.bits.mtech.common.BitsPocConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Customer Microservice which registers with Eureka Server for discovery.
 * 
 * @author Tushar Phadke
 */ 
@EnableAutoConfiguration
@EnableDiscoveryClient
@ComponentScan("edu.bits.mtech.order")
public class OrderServer {

  public static void main(String[] args) {
    // Tell Boot to look for registration-server.yml
    System.setProperty("spring.config.name", BitsPocConstants.ORDER_SERVER_NAME);
    SpringApplication.run(OrderServer.class, args);
  }
}
