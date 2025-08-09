package org.sushant.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try(InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties not found in classpath");
            }

            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
