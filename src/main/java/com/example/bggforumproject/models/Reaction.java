package com.example.bggforumproject.models;

import com.example.bggforumproject.models.base.BaseEntity;
import com.example.bggforumproject.models.contracts.Ownable;
import com.example.bggforumproject.models.enums.ReactionType;
import jakarta.persistence.*;

@Entity
@Table(name = "reactions")
public class Reaction extends BaseEntity implements Ownable {

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    @ManyToOne()
    @JoinColumn(name = "post_id", nullable = false)
    private Post postId;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

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
