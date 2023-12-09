package servlet;

import model.Like;
import repository.LikeRepository;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/likes/*")
public class LikeServlet extends HttpServlet {

    private LikeRepository likeRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        likeRepository = new LikeRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")){
            List<Like> users = likeRepository.getAllLike();
            resp.getWriter().write(users.toString());
        } else {
            try{
                Long userId = Long.parseLong(pathInfo.substring(1));
                Like user = likeRepository.getLikeById(userId);

                if (user != null){
                    resp.getWriter().write(user.toString());
                } else {
                    resp.getWriter().write("User not found!");
                }
            } catch (NumberFormatException e){
                resp.getWriter().write("Invalid user ID format");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = Long.parseLong(req.getParameter("userId"));
        Long postId = Long.parseLong(req.getParameter("postId"));

        Like newLike = new Like();
        newLike.setUserId(userId);
        newLike.setPostId(postId);

        Like addLike = likeRepository.addLike(newLike);

        if (addLike != null){
            resp.getWriter().write("Like added: " + addLike.toString());
        } else {
            resp.getWriter().write("Failed to like!");
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
                    Long userId = Long.parseLong(request.getParameter("userId"));
                    Long postId = Long.parseLong(request.getParameter("postId"));

                    existingLike.setUserId(userId);
                    existingLike.setPostId(postId);

                    likeRepository.updateLike(existingLike);

                    response.getWriter().write("Like updated: " + existingLike.toString());
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
}
