package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/post_m_service_db";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "postgres";

    private static final String POST_TABLE = "CREATE TABLE IF NOT EXISTS POSTS (" +
            "POST_ID SERIAL PRIMARY KEY," +
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
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute(POST_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_QUERY);

            while (resultSet.next()) {
                posts.add(mapResultSetToPost(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public Post getPostById(Long postId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

            preparedStatement.setLong(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToPost(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Post addPost(Post post) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, post.getAuthorId());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setString(3, post.getCreationTime());
            preparedStatement.setInt(4,post.getLikes());
            preparedStatement.setInt(5, post.getComments());
            preparedStatement.setInt(6, post.getViews());
            preparedStatement.setString(7, post.getTags());
            preparedStatement.executeUpdate();

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    post.setPostId(generatedKeys.getLong(1));
                    return post;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void updatePost(Post post) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {

            preparedStatement.setLong(1, post.getAuthorId());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setString(3, post.getCreationTime());
            preparedStatement.setInt(4,post.getLikes());
            preparedStatement.setInt(5, post.getComments());
            preparedStatement.setInt(6, post.getViews());
            preparedStatement.setString(7, post.getTags());
            preparedStatement.setLong(8, post.getPostId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Post mapResultSetToPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setPostId(resultSet.getLong("POST_ID"));
        post.setAuthorId(resultSet.getLong("AUTHOR_ID"));
        post.setContent(resultSet.getString("CONTENT"));
        post.setCreationTime(resultSet.getString("CREATION_TIME"));
        post.setLikes(resultSet.getInt("LIKES"));
        post.setComments(resultSet.getInt("COMMENTS"));
        post.setViews(resultSet.getInt("VIEWS"));
        post.setTags(resultSet.getString("TAGS"));
        return post;
    }

    public void deletePost(Long postId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)) {

            preparedStatement.setLong(1, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Post createPostFromRequest(ResultSet resultSet) {
        try {
            Post newPost = new Post();
            newPost.setAuthorId(resultSet.getLong("author_id"));
            newPost.setContent(resultSet.getString("content"));
            newPost.setCreationTime(resultSet.getString("creation_time"));
            newPost.setLikes(resultSet.getInt("likes"));
            newPost.setComments(resultSet.getInt("comments"));
            newPost.setViews(resultSet.getInt("views"));
            newPost.setTags(resultSet.getString("tags"));
            return newPost;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
