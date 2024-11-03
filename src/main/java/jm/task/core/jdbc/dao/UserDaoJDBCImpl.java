package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    private static final String SQL_QUERY_CREATE_TABLE_USERS = """
            CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                name VARCHAR(30) NOT NULL,
                                                last_name VARCHAR(30),
                                                age TINYINT)""";
    private static final String SQL_QUERY_DROP_TABLE_USERS = "DROP TABLE IF EXISTS users";
    private static final String SQL_QUERY_REMOVE_USER = "DELETE FROM users WHERE id = ?";
    private static final String SQL_QUERY_CLEAN_TABLE_USERS = "TRUNCATE TABLE users";
    private static final String SQL_QUERY_SELECT_ALL_USERS = "SELECT id, name, last_name, age FROM users";
    private static final String SQL_QUERY_SAVE_USER_IN_TABLE = "INSERT INTO users (name, last_name, age) VALUES (?, ?, ?)";


    public UserDaoJDBCImpl() {
    }

    public void createUsersTable() {
        try (var connection = Util.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(SQL_QUERY_CREATE_TABLE_USERS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropUsersTable() {
        try (var connection = Util.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(SQL_QUERY_DROP_TABLE_USERS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
                    В качестве примера ручной обработки транзакций
    * */
    public void saveUser(String name, String lastName, byte age) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = Util.getConnection();
            preparedStatement = connection.prepareStatement(SQL_QUERY_SAVE_USER_IN_TABLE);
            connection.setAutoCommit(false);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public void removeUserById(long id) {
        try (var connection = Util.getConnection();
             var prepareStatement = connection.prepareStatement(SQL_QUERY_REMOVE_USER)) {
            prepareStatement.setLong(1, id);
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        try (var connection = Util.getConnection();
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(SQL_QUERY_SELECT_ALL_USERS);
            while (resultSet.next()) {
                User user;
                usersList.add(user = new User(resultSet.getString("name"),
                        resultSet.getString("last_name"),
                        resultSet.getByte("age")));
                user.setId(resultSet.getLong("id"));
            }
            return usersList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanUsersTable() {
        try (var connection = Util.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(SQL_QUERY_CLEAN_TABLE_USERS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
