package repository;

import model.Like;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikeRepository {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/like_service_db";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "postgres";

    private static final String LIKE_TABLE = "CREATE TABLE IF NOT EXISTS LIKE (" +
            "ID SERIAL PRIMARY KEY," +
            "USER_ID INT," +
            "POST_ID INT" +
            ")";

    public LikeRepository(){
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute(LIKE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Like getLikeById(Long id) {
        String query = "SELECT * FROM COMMENT WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToLike(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Like> getAllLike() {
        List<Like> likes = new ArrayList<>();
        String query = "SELECT * FROM LIKE";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                likes.add(mapResultSetToLike(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likes;
    }

    public Like addLike(Like like) {
        String query = "INSERT INTO LIKE (USER_ID, POST_ID) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, like.getUserId());
            preparedStatement.setLong(2, like.getPostId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    like.setLikeId(generatedKeys.getLong(1));
                    return like;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Like mapResultSetToLike(ResultSet resultSet) throws SQLException {
        Like like = new Like();
        like.setLikeId(resultSet.getLong("ID"));
        like.setUserId(resultSet.getLong("USER_ID"));
        like.setPostId(resultSet.getLong("POST_ID"));

        return like;
    }

    public void updateLike(Like like) {
        String query = "UPDATE LIKE SET USER_ID=?, POST_ID=? WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, like.getUserId());
            preparedStatement.setLong(2, like.getPostId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLike(Long likeId) {
        String query = "DELETE FROM LIKE WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, likeId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
