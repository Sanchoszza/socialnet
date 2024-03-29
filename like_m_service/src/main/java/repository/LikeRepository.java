package repository;

import model.Like;
import utils.PropertyManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikeRepository {

//    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/like_service_db";
//    private static final String JDBC_USER = "postgres";
//    private static final String JDBC_PASSWORD = "postgres";

    private String JDBC_URL;
    private String JDBC_USER;
    private String JDBC_PASSWORD;

    private PostRepository postRepository;

    private static final String LIKE_TABLE = "CREATE TABLE IF NOT EXISTS LIKES (" +
            "ID SERIAL PRIMARY KEY," +
            "USER_ID INT," +
            "POST_ID INT" +
            ")";

    public LikeRepository(){
        JDBC_URL = PropertyManager.getPropertyAsString("db.connection.string",
                "jdbc:postgresql://localhost:5432/like_service_db");
        JDBC_USER = PropertyManager.getPropertyAsString("db.connection.user", "postgres");
        JDBC_PASSWORD = PropertyManager.getPropertyAsString("db.connection.password", "postgres");
        postRepository = new PostRepository();
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
        String query = "SELECT * FROM LIKES WHERE ID=?";
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
        String query = "SELECT * FROM LIKES";
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
        String query = "INSERT INTO LIKES (USER_ID, POST_ID) VALUES (?, ?)";
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
        String query = "UPDATE LIKES SET USER_ID=?, POST_ID=? WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, like.getUserId());
            preparedStatement.setLong(2, like.getPostId());

            preparedStatement.executeUpdate();

            // После обновления лайка также нужно обновить количество лайков для поста
            updatePostLikesCount(like.getPostId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePostLikesCount(Long postId) {
        // Получаем текущее количество лайков для поста
        int currentLikes = postRepository.getLikesForPost(postId);
        // Обновляем количество лайков для поста
        int updatedLikes = currentLikes + 1; // Предположим, что это увеличивает количество лайков на 1
        // Обновляем количество лайков для поста в репозитории постов
        postRepository.updatePostLikes(postId, updatedLikes);
    }



    public void deleteLike(Long likeId) {
        String query = "DELETE FROM LIKES WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, likeId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                // Если лайк успешно удален, обновляем количество лайков для поста
                Like like = getLikeById(likeId);
                if (like != null) {
                    updatePostLikesCount(like.getPostId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
