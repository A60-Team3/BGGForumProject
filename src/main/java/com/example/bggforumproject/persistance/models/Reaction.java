package com.example.bggforumproject.persistance.models;

import com.example.bggforumproject.persistance.models.base.BaseEntity;
import com.example.bggforumproject.persistance.models.enums.ReactionType;

public class Reaction extends BaseEntity {

    private Post postId;
    private User userId;
    private ReactionType reactionType;

    public Reaction() {
    }

    public Post getPostId() {
        return postId;
    }

    public void setPostId(Post postId) {
        this.postId = postId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}
