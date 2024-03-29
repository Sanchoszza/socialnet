package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Like;
import repository.LikeRepository;
import repository.PostRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/likes/*")
public class LikeServlet extends HttpServlet {

    private LikeRepository likeRepository;
    private PostRepository postRepository;

    @Override
    public void init() {
        likeRepository = new LikeRepository();
        postRepository = new PostRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Like> likes = likeRepository.getAllLike();
            String jsonResponse = convertLikeToJson(likes);
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        } else {
            try {
                Long likeId = Long.parseLong(pathInfo.substring(1));
                Like like = likeRepository.getLikeById(likeId);

                if (like != null) {
                    String jsonResponse = convertObjectToJson(like);
                    response.setContentType("application/json");
                    response.getWriter().write(jsonResponse);
                } else {
                    response.getWriter().write("Like not found!");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid like ID format");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        String json = stringBuilder.toString();
        System.out.println("Received JSON: " + json);

        ObjectMapper objectMapper = new ObjectMapper();
        Like newLike = objectMapper.readValue(json, Like.class);

        Like addedLike = likeRepository.addLike(newLike);

        if (addedLike != null) {
            Long postId = addedLike.getPostId();
            updatePostLikes(postId, 1); // Увеличиваем количество лайков на 1
            response.getWriter().write(objectMapper.writeValueAsString(addedLike));
        } else {
            response.getWriter().write("Failed to add post!");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long likeId = Long.parseLong(pathInfo.substring(1));
                Like existingLike = likeRepository.getLikeById(likeId);

                if (existingLike != null) {
                    Like updatedLike = updateLikeFromRequest(existingLike, request);

                    likeRepository.updateLike(updatedLike);

                    response.getWriter().write("Like updated: " + updatedLike.toString());
                } else {
                    response.getWriter().write("Like not found");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid Like ID format");
            }
        } else {
            response.getWriter().write("Invalid request for updating Like");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long likeId = Long.parseLong(pathInfo.substring(1));
                Like existingLike = likeRepository.getLikeById(likeId);

                if (existingLike != null) {
                    likeRepository.deleteLike(likeId);
                    Long postId = existingLike.getPostId();
                    updatePostLikes(postId, -1); // Уменьшаем количество лайков на 1
                    response.getWriter().write("Like deleted: " + existingLike.toString());
                } else {
                    response.getWriter().write("Like not found");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid Like ID format");
            }
        } else {
            response.getWriter().write("Invalid request for deleting Like");
        }
    }

    private Like updateLikeFromRequest(Like existingLike, HttpServletRequest request) {
        existingLike.setUserId(Long.parseLong(request.getParameter("userId")));
        existingLike.setPostId(Long.parseLong(request.getParameter("postId")));

        return existingLike;
    }

    private String convertLikeToJson(List<Like> likes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(likes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting list to JSON";
        }
    }

    private String convertObjectToJson(Like like) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(like);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting object to JSON";
        }
    }

    private void updatePostLikes(Long postId, int delta) {
        // Получаем текущее количество лайков для поста
        int currentLikes = postRepository.getLikesForPost(postId);
        // Обновляем количество лайков для поста с учетом приращения
        int updatedLikes = currentLikes + delta;
        // Обновляем количество лайков для поста в репозитории
        postRepository.updatePostLikes(postId, updatedLikes);
    }
}
