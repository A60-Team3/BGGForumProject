package com.example.bggforumproject.presentation.dtos;

import com.example.bggforumproject.persistance.models.enums.ReactionType;
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
