package repository;

import model.User;
import utils.PropertyManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

//    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/user_m_service_db";
//    private static final String JDBC_USER = "postgres";
//    private static final String JDBC_PASSWORD = "postgres";

    private String JDBC_URL;
    private String JDBC_USER;
    private String JDBC_PASSWORD;

    private static final String USER_TABLE = "CREATE TABLE IF NOT EXISTS USERS (" +
            "ID SERIAL PRIMARY KEY," +
            "FIRST_NAME VARCHAR(255)," +
            "LAST_NAME VARCHAR(255)," +
            "EMAIL VARCHAR(255)," +
            "PASSWORD_HASH VARCHAR(255)," +
            "REGISTRATION_DATE TIMESTAMP" +
            ")";

    public UserRepository() {
        JDBC_URL = PropertyManager.getPropertyAsString("db.connection.string",
                "jdbc:postgresql://localhost:5432/user_m_service_db");
        JDBC_USER = PropertyManager.getPropertyAsString("db.connection.user", "postgres");
        JDBC_PASSWORD = PropertyManager.getPropertyAsString("db.connection.password", "postgres");
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute(USER_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public User getUserById(Long id) {
        String query = "SELECT * FROM USERS WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM USERS";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User registerUser(User user) {
        String query = "INSERT INTO USERS (FIRST_NAME, LAST_NAME, EMAIL, PASSWORD_HASH, REGISTRATION_DATE) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPasswordHash());
            preparedStatement.setTimestamp(5, new Timestamp(user.getRegistrationDate().getTime()));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserID(generatedKeys.getLong(1));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserID(resultSet.getLong("ID"));
        user.setFirstName(resultSet.getString("FIRST_NAME"));
        user.setLastName(resultSet.getString("LAST_NAME"));
        user.setEmail(resultSet.getString("EMAIL"));
        user.setPasswordHash(resultSet.getString("PASSWORD_HASH"));
        user.setRegistrationDate(resultSet.getTimestamp("REGISTRATION_DATE"));
        return user;
    }


    public void updateUser(User user) {
        String query = "UPDATE USERS SET FIRST_NAME=?, LAST_NAME=?, EMAIL=?, PASSWORD_HASH=? WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPasswordHash());
            preparedStatement.setLong(5, user.getUserID());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(Long userId) {
        String query = "DELETE FROM USERS WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, userId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
