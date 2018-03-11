/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configurator for POC.
 *
 * @author Tushar Phadke
 */
public final class BitsConfigurator {

    private static final Logger logger = Logger.getLogger(BitsConfigurator.class.getName());
    private static Properties properties = new Properties();
    static {
        initialize();
    }

    /**
     * Private constructor to avoid instance creation.
     */
    private BitsConfigurator() {

    }

    private static void initialize() {
        try {
            String envFile = System.getProperty("config.file");

            if (envFile == null || envFile.trim().length() == 0) {
                InputStream is = BitsConfigurator.class.getClassLoader().getResourceAsStream("bits-test-config.properties");
                properties.load(is);
            } else {
                File configFile = new File(envFile);
                if (!configFile.exists()) {
                    throw new IllegalStateException("Invalid properties file");
                }
                properties.load(new FileInputStream(configFile));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize configuration", e);
            throw new IllegalStateException("Failed to initialize configuration");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getIntProperty(String key) {

        return Integer.parseInt(getProperty(key));
    }

    public static int getIntProperty(String key, int defaultValue) {
        return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
    }
}