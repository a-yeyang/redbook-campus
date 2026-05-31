package com.yeyang.redbook.distributed.id.generator.biz.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class PropertyFactory {
    private static final Logger logger = LoggerFactory.getLogger(PropertyFactory.class);
    private static final String[] OVERRIDABLE_KEYS = {
            "leaf.name",
            "leaf.segment.enable",
            "leaf.jdbc.url",
            "leaf.jdbc.username",
            "leaf.jdbc.password",
            "leaf.snowflake.enable",
            "leaf.snowflake.worker-id"
    };
    private static final Properties prop = new Properties();

    static {
        try {
            prop.load(PropertyFactory.class.getClassLoader().getResourceAsStream("leaf.properties"));
        } catch (IOException e) {
            logger.warn("Load Properties Ex", e);
        }
    }

    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(prop);
        for (String key : OVERRIDABLE_KEYS) {
            override(properties, key);
        }
        return properties;
    }

    private static void override(Properties properties, String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            properties.setProperty(key, systemValue);
            return;
        }

        String envKey = key.toUpperCase(Locale.ROOT)
                .replace('.', '_')
                .replace('-', '_');
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            properties.setProperty(key, envValue);
        }
    }
}
