package com.example.bggforumproject.presentation.dtos;

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
