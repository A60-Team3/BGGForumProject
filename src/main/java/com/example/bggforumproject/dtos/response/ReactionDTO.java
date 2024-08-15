package com.example.bggforumproject.dtos.response;

import com.example.bggforumproject.models.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
@Schema(description = "This DTO holds info necessary for adding reaction po post.",
        allowableValues ={"reactionType"})
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
