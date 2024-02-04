package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Like;
import repository.LikeRepository;

import javax.servlet.ServletConfig;
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

    @Override
    public void init(ServletConfig config) throws ServletException {
        likeRepository = new LikeRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")){
            List<Like> likes = likeRepository.getAllLike();
            String jsonResponse = convertLikeToJson(likes);
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        } else {
            try{
                Long likeId = Long.parseLong(pathInfo.substring(1));
                Like like = likeRepository.getLikeById(likeId);

                if (like != null){
                    String jsonResponse = convertObjectToJson(like);
                    response.setContentType("application/json");
                    response.getWriter().write(jsonResponse);
                } else {
                    response.getWriter().write("Like not found!");
                }
            } catch (NumberFormatException e){
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
                    Like updatedPost = updateLikeFromRequest(existingLike, request);

                    likeRepository.updateLike(updatedPost);

                    response.getWriter().write("Like updated: " + updatedPost.toString());
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

    private String convertLikeToJson(List<Like> posts) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(posts);
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
}
