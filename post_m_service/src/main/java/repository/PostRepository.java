package repository;

import model.Comment;
import model.Post;
import utils.PropertyManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    private String JDBC_URL_POST;
    private String JDBC_USER;
    private String JDBC_PASSWORD;
    private String JDBC_URL_COMMENT;
    private String JDBC_URL_LIKE;

    private static final String POST_TABLE = "CREATE TABLE IF NOT EXISTS posts (" +
            "post_id SERIAL PRIMARY KEY," +
            "author_id BIGINT," +
            "content VARCHAR(255)," +
            "creation_time VARCHAR(255)," +
            "likes INT," +
            "comments INT," +
            "views INT," +
            "tags VARCHAR(255)" +
            ")";

    private static final String SELECT_ALL_QUERY = "SELECT * FROM posts";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM posts WHERE post_id=?";
    private static final String INSERT_QUERY = "INSERT INTO posts (author_id, content, creation_time, likes, comments, views, tags) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE posts SET author_id=?, content=?, creation_time=?, likes_count=?, comments=?, views=?, tags=? WHERE post_id=?";
    private static final String DELETE_QUERY = "DELETE FROM posts WHERE post_id=?";
    private static final String SELECT_COMMENTS_BY_POST_ID_QUERY = "SELECT * FROM comment WHERE post_id=?";
    private static final String INSERT_COMMENT_QUERY = "INSERT INTO comment (post_id, content, creation_time) VALUES (?, ?, ?)";
    private static final String DELETE_COMMENTS_FOR_POST_QUERY = "DELETE FROM comment WHERE post_id=?";
    private static final String UPDATE_POST_QUERY = "UPDATE posts SET author_id=?, content=?, creation_time=?, likes=?, views=?, tags=? WHERE post_id=?";

    public PostRepository() {
        JDBC_URL_POST = PropertyManager.getPropertyAsString("db.connection.string",
                "jdbc:postgresql://localhost:5432/post_m_service_db");
        JDBC_URL_COMMENT = PropertyManager.getPropertyAsString("db.connection.string",
                "jdbc:postgresql://localhost:5432/comment_service_db");
        JDBC_URL_LIKE = PropertyManager.getPropertyAsString("db.connection.string",
                "jdbc:postgresql://localhost:5432/like_service_db");
        JDBC_USER = PropertyManager.getPropertyAsString("db.connection.user", "postgres");
        JDBC_PASSWORD = PropertyManager.getPropertyAsString("db.connection.password", "postgres");
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_POST, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute(POST_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL_POST, JDBC_USER, JDBC_PASSWORD);
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
        try (Connection connection = DriverManager.getConnection(JDBC_URL_POST, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

            preparedStatement.setLong(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Post post = mapResultSetToPost(resultSet);
                post.setComments(loadCommentsForPost(postId));
                return post;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Post addPost(Post post) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_POST, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, post.getAuthorId());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setString(3, post.getCreationTime());
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, post.getComments().size());
            preparedStatement.setInt(6, post.getViews());
            preparedStatement.setString(7, post.getTags());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                post.setPostId(generatedKeys.getLong(1));
            }

            for (Comment comment : post.getComments()) {
                saveCommentForPost(comment, post.getPostId());
            }

            return post;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveCommentForPost(Comment comment, Long postId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_COMMENT, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COMMENT_QUERY)) {

            preparedStatement.setLong(1, postId);
            preparedStatement.setLong(2, comment.getAuthorId());
            preparedStatement.setString(3, comment.getContent());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePost(Post post) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_POST, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POST_QUERY)) {

            preparedStatement.setLong(1, post.getAuthorId());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setString(3, post.getCreationTime());
            preparedStatement.setInt(4, post.getLikes());
            preparedStatement.setInt(5, post.getViews());
            preparedStatement.setString(6, post.getTags());
            preparedStatement.setLong(7, post.getPostId());
            preparedStatement.executeUpdate();

            updateCommentsForPost(post.getPostId(), post.getComments());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCommentsForPost(Long postId, List<Comment> comments) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_POST, JDBC_USER, JDBC_PASSWORD)) {
            deleteCommentsForPost(postId, connection);
            addCommentsForPost(postId, comments, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCommentsForPost(Long postId, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COMMENTS_FOR_POST_QUERY)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.executeUpdate();
        }
    }

    private void addCommentsForPost(Long postId, List<Comment> comments, Connection connection) throws SQLException {
        for (Comment comment : comments) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COMMENT_QUERY)) {
                preparedStatement.setLong(1, postId);
                preparedStatement.setLong(2, comment.getAuthorId());
                preparedStatement.setString(3, comment.getContent());
                preparedStatement.executeUpdate();
            }
        }
    }

    private Post mapResultSetToPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setPostId(resultSet.getLong("post_id"));
        post.setAuthorId(resultSet.getLong("author_id"));
        post.setContent(resultSet.getString("content"));
        post.setCreationTime(resultSet.getString("creation_time"));
        post.setLikes(resultSet.getInt("likes"));
        post.setViews(resultSet.getInt("views"));
        post.setTags(resultSet.getString("tags"));
        post.setComments(loadCommentsForPost(post.getPostId()));
        return post;
    }

    private Comment mapResultSetToComment(ResultSet resultSet) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(resultSet.getLong("id"));
        comment.setPostId(resultSet.getLong("post_id"));
        comment.setAuthorId(resultSet.getLong("author_id"));
        comment.setContent(resultSet.getString("content"));
        return comment;
    }

    public List<Comment> loadCommentsForPost(Long postId) {
        List<Comment> comments = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL_COMMENT, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COMMENTS_BY_POST_ID_QUERY)) {

            preparedStatement.setLong(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                comments.add(mapResultSetToComment(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public void deletePost(Long postId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_POST, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)) {

            preparedStatement.setLong(1, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updatePostLikes(Long postId, int likes) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_POST, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE posts SET likes = ? WHERE post_id = ?")) {

            preparedStatement.setInt(1, likes);
            preparedStatement.setLong(2, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLikesForPost(Long postId) {
        int likes = 0;
        try (Connection connection = DriverManager.getConnection(JDBC_URL_LIKE, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS likes FROM likes WHERE post_id = ?")) {

            preparedStatement.setLong(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                likes = resultSet.getInt("likes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likes;
    }


}
