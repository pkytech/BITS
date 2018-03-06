package edu.bits.mtech.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka registration server which works as registry
 * 
 * @author Tushar Phadke
 */ 
@SpringBootApplication
@EnableEurekaServer
public class EurekaServer {

  public static void main(String[] args) {
    // Tell Boot to look for registration-server.yml
    System.setProperty("spring.config.name", "eureka-registration-server");
    SpringApplication.run(EurekaServer.class, args);
  }
}