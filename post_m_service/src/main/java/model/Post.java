package model;

import java.util.List;

public class Post {

    private Long postId;
    private Long authorId;
    private String content;
    private String creationTime;
    private int likes;
    private int comments;
    private int views;
    private String tags;

    public Post() {
    }

    public Post(Long postId, Long authorId, String content, String creationTime,
                List<String> attachment, int likes, int comments, int views, String tags) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.creationTime = creationTime;
        this.likes = likes;
        this.comments = comments;
        this.views = views;
        this.tags = tags;
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

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }


    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
