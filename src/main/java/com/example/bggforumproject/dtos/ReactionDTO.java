package com.example.bggforumproject.dtos;

import com.example.bggforumproject.models.enums.ReactionType;
import jakarta.validation.constraints.NotNull;

public class ReactionDTO {

    @NotNull(message = "Reaction type cannot be empty")
    private ReactionType reactionType;

    public ReactionDTO() {
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}
