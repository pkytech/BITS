package edu.bits.mtech.billing;

import edu.bits.mtech.common.BitsPocConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Billing Microservice which registers with Eureka Server for discovery.
 * 
 * @author Tushar Phadke
 */
@EnableAutoConfiguration
@EnableDiscoveryClient
@ComponentScan("edu.bits.mtech.billing")
public class BillingServer {

  public static void main(String[] args) {
    // Tell Boot to look for registration-server.yml
    System.setProperty("spring.config.name", BitsPocConstants.BILLING_SERVER_NAME);
    SpringApplication.run(BillingServer.class, args);
  }
}
