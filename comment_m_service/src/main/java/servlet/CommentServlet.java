package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Comment;
import repository.CommentRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/comment/*")
public class CommentServlet extends HttpServlet {
    private CommentRepository commentRepository;

    @Override
    public void init() {
        commentRepository = new CommentRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Comment> comments = commentRepository.getAllComments();
            String jsonResponse = convertCommentToJson(comments);
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        } else {
            try {
                Long commentId = Long.parseLong(pathInfo.substring(1));
                Comment comment = commentRepository.getCommentById(commentId);

                if (comment != null) {
                    String jsonResponse = convertObjectToJson(comment);
                    response.setContentType("application/json");
                    response.getWriter().write(jsonResponse);
                } else {
                    response.getWriter().write("Comment not found!");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid comment ID format");
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
        Comment newComment = objectMapper.readValue(json, Comment.class);


        Comment addedComment = commentRepository.addComment(newComment);

        if (addedComment != null) {
            response.getWriter().write(objectMapper.writeValueAsString(addedComment));
        } else {
            response.getWriter().write("Failed to add comment!");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long commentId = Long.parseLong(pathInfo.substring(1));
                Comment existingComment = commentRepository.getCommentById(commentId);

                if (existingComment != null) {
                    Comment updateComment = updateCommentFromRequest(existingComment, request);

                    commentRepository.updateComment(updateComment);

                    response.getWriter().write("Comment updated: " + updateComment.toString());
                } else {
                    response.getWriter().write("Comment not found");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid comment ID format");
            }
        } else {
            response.getWriter().write("Invalid request for updating comment");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long commentId = Long.parseLong(pathInfo.substring(1));
                Comment existingComment = commentRepository.getCommentById(commentId);

                if (existingComment != null) {
                    commentRepository.deleteComment(commentId);

                    response.getWriter().write("Comment deleted: " + existingComment.toString());
                } else {
                    response.getWriter().write("Comment not found");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid comment ID format");
            }
        } else {
            response.getWriter().write("Invalid request for deleting comment");
        }
    }

    private Comment updateCommentFromRequest(Comment existingComment, HttpServletRequest request) {
        existingComment.setAuthorId(Long.parseLong(request.getParameter("authorId")));
        existingComment.setContent(request.getParameter("content"));
        existingComment.setLikes(request.getIntHeader("likes"));

        return existingComment;
    }

    private String convertCommentToJson(List<Comment> comments) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(comments);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting list to JSON";
        }
    }

    private String convertObjectToJson(Comment comment) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting object to JSON";
        }
    }
}
