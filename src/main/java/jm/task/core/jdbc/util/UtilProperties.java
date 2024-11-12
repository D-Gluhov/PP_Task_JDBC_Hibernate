package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.Properties;

public final class UtilProperties {
    private static final Properties PROPERTIES = new Properties();
    private static final Configuration CONFIGURATION = new Configuration();

    static {
        loadProperties();
        loadConfiguration();
    }

    private UtilProperties() {}

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static Configuration getConfiguration() {
        return CONFIGURATION;
    }

    private static void loadProperties() {
        try (var resourceAsStream = Util.class.getClassLoader().getResourceAsStream("application.hibernate.properties")) {
            PROPERTIES.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadConfiguration() {
        CONFIGURATION.setProperties(PROPERTIES);
        CONFIGURATION.addAnnotatedClass(User.class);
    }
}
