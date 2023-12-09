package model;

public class Like {

    private Long likeId;
    private Long userId;
    private Long postId;

    public Like() {
    }

    public Like(Long likeId, Long userId, Long postId) {
        this.likeId = likeId;
        this.userId = userId;
        this.postId = postId;
    }

    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
