package com.example.bggforumproject.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "This DTO holds info about a relation.",
        allowableValues = {"reactionType", "reactionMakerFullName"})
public class ReactionOutDTO {
    private String reactionType;
    private String reactionMakerFullName;

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public String getReactionMakerFullName() {
        return reactionMakerFullName;
    }

    public void setReactionMakerFullName(String reactionMakerFullName) {
        this.reactionMakerFullName = reactionMakerFullName;
    }
}
