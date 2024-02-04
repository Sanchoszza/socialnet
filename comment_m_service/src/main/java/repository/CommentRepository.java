package repository;

import model.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/comment_service_db";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "postgres";

    private static final String COMMENT_TABLE = "CREATE TABLE IF NOT EXISTS COMMENT (" +
            "ID SERIAL PRIMARY KEY," +
            "AUTHOR_ID INT," +
            "CONTENT TEXT," +
            "LIKES INT" +
            ")";
//    "CREATION_TIME TIMESTAMP" +

    public CommentRepository() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute(COMMENT_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Comment getCommentById(Long id) {
        String query = "SELECT * FROM COMMENT WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToComment(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM COMMENT";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                comments.add(mapResultSetToComment(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public Comment addComment(Comment comment) {
//        , CREATION_TIME
        String query = "INSERT INTO COMMENT (AUTHOR_ID, CONTENT, LIKES) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, comment.getAuthorId());
            preparedStatement.setString(2, comment.getContent());
            preparedStatement.setInt(3, comment.getLikes());
//            preparedStatement.setTimestamp(4, new Timestamp(comment.getCreationTime().getTime()));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    comment.setCommentId(generatedKeys.getLong(1));
                    return comment;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Comment mapResultSetToComment(ResultSet resultSet) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(resultSet.getLong("ID"));
        comment.setAuthorId(resultSet.getLong("AUTHOR_ID"));
        comment.setContent(resultSet.getString("CONTENT"));
        comment.setLikes(resultSet.getInt("LIKES"));
//        comment.setCreationTime(resultSet.getTimestamp("CREATION_TIME"));
        return comment;
    }

    public void updateComment(Comment comment) {
//        CREATION_TIME=?
        String query = "UPDATE COMMENT SET AUTHOR_ID=?, CONTENT=?, LIKES=? WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, comment.getAuthorId());
            preparedStatement.setString(2, comment.getContent());
            preparedStatement.setInt(3, comment.getLikes());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteComment(Long commentId) {
        String query = "DELETE FROM COMMENT WHERE ID=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, commentId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
