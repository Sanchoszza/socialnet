package model;

import java.time.LocalDateTime;

public class Comment {

    private Long commentId;
    private Long authorId;
    private String content;
    private LocalDateTime creationTime;
    private int likes;

    public Comment() {
    }

    public Comment(Long commentId, Long authorId, String content, LocalDateTime creationTime, int likes) {
        this.commentId = commentId;
        this.authorId = authorId;
        this.content = content;
        this.creationTime = creationTime;
        this.likes = likes;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
