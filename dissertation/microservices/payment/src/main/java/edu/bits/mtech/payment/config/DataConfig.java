/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.payment.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * JPA Configuration class.
 *
 * @author Tushar Phadke
 */
@Configuration
@EnableTransactionManagement
@EntityScan("edu.bits.mtech.payment.db.bo")
@EnableAutoConfiguration
public class DataConfig {

    private static final Logger logger = Logger.getLogger(DataConfig.class.getName());

    @Bean
    SessionFactory buildSessionfactory() {

        SessionFactory sessionFactory = null;
        try {
            final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure() // configures settings from hibernate.cfg.xml
                    .build();
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to initialize hibernate", e);
            throw new IllegalStateException("Failed to initialize hibernate", e);
        }
        return sessionFactory;
    }
}
