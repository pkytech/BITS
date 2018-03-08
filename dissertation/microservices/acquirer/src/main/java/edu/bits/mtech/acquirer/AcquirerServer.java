package edu.bits.mtech.acquirer;

import edu.bits.mtech.common.BitsPocConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Acquirer Microservice which registers with Eureka Server for discovery.
 * 
 * @author Tushar Phadke
 */ 
@EnableAutoConfiguration
@EnableDiscoveryClient
@ComponentScan("edu.bits.mtech.acquirer")
public class AcquirerServer {

  public static void main(String[] args) {
    // Tell Boot to look for registration-server.yml
    System.setProperty("spring.config.name", BitsPocConstants.ACQUIRER_SERVER_NAME);
    SpringApplication.run(AcquirerServer.class, args);
  }
}
