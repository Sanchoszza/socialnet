package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Post;
import repository.PostRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/posts/*")
public class PostServlet extends HttpServlet {

    private PostRepository postRepository;

    @Override
    public void init() {
        postRepository = new PostRepository();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Post> posts = postRepository.getAllPosts();

            String jsonResponse = convertPostsToJson(posts);
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        } else {
            try {
                Long postId = Long.parseLong(pathInfo.substring(1));
                Post post = postRepository.getPostById(postId);

                if (post != null) {
                    String jsonResponse = convertObjectToJson(post);
                    response.setContentType("application/json");
                    response.getWriter().write(jsonResponse);
                } else {
                    response.getWriter().write("Post not found");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid post ID format" + e);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        String json = stringBuilder.toString();
        System.out.println("Received JSON: " + json);

        ObjectMapper objectMapper = new ObjectMapper();
        Post newPost = objectMapper.readValue(json, Post.class);

        Post addedPost = postRepository.addPost(newPost);

        if (addedPost != null) {
            response.getWriter().write(objectMapper.writeValueAsString(addedPost));
        } else {
            response.getWriter().write("Failed to add post!");
        }

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long postId = Long.parseLong(pathInfo.substring(1));
                Post existingPost = postRepository.getPostById(postId);

                if (existingPost != null) {
                    Post updatedPost = updatePostFromRequest(existingPost, request);

                    postRepository.updatePost(updatedPost);

                    response.getWriter().write("Post updated: " + updatedPost.toString());
                } else {
                    response.getWriter().write("Post not found");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid post ID format");
            }
        } else {
            response.getWriter().write("Invalid request for updating post");
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long postId = Long.parseLong(pathInfo.substring(1));
                Post existingPost = postRepository.getPostById(postId);

                if (existingPost != null) {
                    postRepository.deletePost(postId);

                    response.getWriter().write("Post deleted: " + existingPost.toString());
                } else {
                    response.getWriter().write("Post not found");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid post ID format");
            }
        } else {
            response.getWriter().write("Invalid request for deleting post");
        }
    }

    private String convertPostsToJson(List<Post> posts) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(posts);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting list to JSON";
        }
    }

    private Post createPostFromRequest(HttpServletRequest request) {
        Post newPost = new Post();
        newPost.setContent(request.getParameter("content"));
        newPost.setAuthorId(parseLongOrDefault(request.getParameter("authorId"), 0L));
        newPost.setCreationTime(request.getParameter("creationTime"));
        newPost.setLikes(parseIntOrDefault(request.getParameter("likes"), 0));
        newPost.setComments(parseIntOrDefault(request.getParameter("comments"), 0));
        newPost.setViews(parseIntOrDefault(request.getParameter("views"), 0));
        newPost.setTags(request.getParameter("tags"));

        return newPost;
    }

    private Long parseLongOrDefault(String value, Long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    private Integer parseIntOrDefault(String value, Integer defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }


    private Post updatePostFromRequest(Post existingPost, HttpServletRequest request) {
        existingPost.setContent(request.getParameter("content"));
        existingPost.setAuthorId(Long.parseLong(request.getParameter("authorId")));
        existingPost.setCreationTime(request.getParameter("creationTime"));
        existingPost.setLikes(Integer.parseInt(request.getParameter("likes")));
        existingPost.setComments(Integer.parseInt(request.getParameter("comments")));
        existingPost.setViews(Integer.parseInt(request.getParameter("views")));
        existingPost.setTags(request.getParameter("tags"));

        return existingPost;
    }

    private String convertObjectToJson(Post post) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(post);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting object to JSON";
        }
    }
}
