package jm.task.core.jdbc.util;

import java.io.IOException;
import java.util.Properties;

public final class UtilProperties {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private UtilProperties() {}

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (var resourceAsStream = Util.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
