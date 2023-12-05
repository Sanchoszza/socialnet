package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Post;
import repository.PostRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/posts/*")
public class PostServlet  extends HttpServlet {

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
                response.getWriter().write("Invalid post ID format");
            }
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Post newPost = createPostFromRequest(request);

        postRepository.addPost(newPost);

        response.setStatus(HttpServletResponse.SC_CREATED);
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
                Post existingPost = PostRepository.getPostById(postId);

                if (existingPost != null) {
                    // Удаление поста из репозитория
                    PostRepository.deletePost(postId);

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
        newPost.setAuthorId(Long.parseLong(request.getParameter("authorId")));
        newPost.setCreationTime(request.getParameter("creationTime"));
        newPost.setLikes(Integer.parseInt(request.getParameter("likes")));
        newPost.setComments(Integer.parseInt(request.getParameter("comments")));
        newPost.setViews(Integer.parseInt(request.getParameter("views")));
        newPost.setTags(request.getParameter("tags"));


        return newPost;
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


    private String convertListToJson(List<Post> posts) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(posts);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting list to JSON";
        }
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
