package org.sushant.utils;

import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private final static Properties properties = new Properties();

    public static void load(String configPath) throws IOException {
        try (var inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new IOException("Config file not found in resources: " + configPath);
            }
            properties.load(inputStream);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
