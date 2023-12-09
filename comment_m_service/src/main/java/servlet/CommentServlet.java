package servlet;

import model.Comment;
import repository.CommentRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Comment> comments = commentRepository.getAllComments();
            resp.getWriter().write(comments.toString());
        } else {
            try {
                Long commentId = Long.parseLong(pathInfo.substring(1));
                Comment comment = commentRepository.getCommentById(commentId);

                if (comment != null) {
                    resp.getWriter().write(comment.toString());
                } else {
                    resp.getWriter().write("Comment not found!");
                }
            } catch (NumberFormatException e) {
                resp.getWriter().write("Invalid comment ID format");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long authorID = Long.parseLong(req.getParameter("authorID"));
        String content = req.getParameter("content");

        Comment newComment = new Comment();
        newComment.setAuthorId(authorID);
        newComment.setContent(content);
        newComment.setCreationTime(LocalDateTime.now());
        newComment.setLikes(0);

        Comment addedComment = commentRepository.addComment(newComment);

        if (addedComment != null) {
            resp.getWriter().write("Comment added: " + addedComment.toString());
        } else {
            resp.getWriter().write("Failed to add comment!");
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
                    Long authorID = Long.parseLong(request.getParameter("authorID"));
                    String content = request.getParameter("content");

                    existingComment.setAuthorId(authorID);
                    existingComment.setContent(content);

                    commentRepository.updateComment(existingComment);

                    response.getWriter().write("Comment updated: " + existingComment.toString());
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
}
