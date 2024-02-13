package model;

import java.time.LocalDateTime;
import java.util.Date;

public class Comment {

    private Long commentId;
    private Long postId;
    private Long authorId;
    private String content;
    private int likes;
//    private Date creationTime;

    public Comment() {
    }

    public Comment(Long commentId, Long postId, Long authorId, String content, Date creationTime, int likes) {
        this.commentId = commentId;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.likes = likes;
//        this.creationTime = creationTime;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
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

//    public Date getCreationTime() {
//        return creationTime;
//    }
//
//    public void setCreationTime(Date creationTime) {
//        this.creationTime = creationTime;
//    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
