package com.example.bggforumproject.dtos;

import java.util.List;

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
