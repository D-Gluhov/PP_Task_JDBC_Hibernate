package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Util {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

    private Util() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(UtilProperties.getProperty(URL_KEY),
                    UtilProperties.getProperty(USERNAME_KEY),
                    UtilProperties.getProperty(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
