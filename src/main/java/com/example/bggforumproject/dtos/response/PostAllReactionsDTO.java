package com.example.bggforumproject.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "This DTO holds the post info related to reactions.",
        allowableValues = {"postTitle", "postCreator", "reactions"})
public class PostAllReactionsDTO {
    private String postTitle;
    private String postCreator;
    private List<ReactionOutDTO> reactions;

    public PostAllReactionsDTO(String postTitle, String postCreator, List<ReactionOutDTO> reactions) {
        this.postTitle = postTitle;
        this.postCreator = postCreator;
        this.reactions = reactions;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostCreator() {
        return postCreator;
    }

    public void setPostCreator(String postCreator) {
        this.postCreator = postCreator;
    }

    public List<ReactionOutDTO> getReactions() {
        return reactions;
    }

    public void setReactions(List<ReactionOutDTO> reactions) {
        this.reactions = reactions;
    }
}
