package repository;

import model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    private static final String JDBC_URL = "jdbc:postgresql://post_m_service_db";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "postgres";

    String POST_TABLE = "CREATE TABLE IF NOT EXISTS POST (" +
            "POST_ID IDENTITY PRIMARY KEY," +
            "AUTHOR_ID BIGINT," +
            "CONTENT VARCHAR(255)," +
            "CREATION_TIME VARCHAR(255)," +
            "LIKES INT," +
            "COMMENTS INT," +
            "VIEWS INT," +
            "TAGS VARCHAR(255)" +
            ")";

    private static final String SELECT_ALL_QUERY = "SELECT * FROM posts";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM posts WHERE post_id=?";
    private static final String INSERT_QUERY = "INSERT INTO posts (author_id, content, creation_time, likes, comments, views, tags) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE posts SET author_id=?, content=?, creation_time=?, likes=?, comments=?, views=?, tags=? WHERE post_id=?";
    private static final String DELETE_QUERY = "DELETE FROM posts WHERE post_id=?";

    public PostRepository(){
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {
            statement.execute(POST_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Post post = createPostFromResultSet(resultSet);
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public static Post getPostById(Long postId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

            preparedStatement.setLong(1, postId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return createPostFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addPost(Post post) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {

            setPostValuesInStatement(preparedStatement, post);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePost(Post post) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {

            setPostValuesInStatement(preparedStatement, post);
            preparedStatement.setLong(8, post.getPostId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deletePost(Long postId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)) {

            preparedStatement.setLong(1, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Post createPostFromResultSet(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setPostId(resultSet.getLong("post_id"));
        post.setAuthorId(resultSet.getLong("author_id"));
        post.setContent(resultSet.getString("content"));
        post.setCreationTime(resultSet.getString("creation_time"));
        post.setLikes(resultSet.getInt("likes"));
        post.setComments(resultSet.getInt("comments"));
        post.setViews(resultSet.getInt("views"));


        return post;
    }

    private static void setPostValuesInStatement(PreparedStatement preparedStatement, Post post) throws SQLException {
        preparedStatement.setLong(1, post.getAuthorId());
        preparedStatement.setString(2, post.getContent());
        preparedStatement.setString(3, post.getCreationTime());
        preparedStatement.setInt(4,post.getLikes());
        preparedStatement.setInt(5, post.getComments());
        preparedStatement.setInt(6, post.getViews());
    }
}
