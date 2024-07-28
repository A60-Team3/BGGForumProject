package com.example.bggforumproject.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "This DTO holds comment info available to authenticated users.",
        allowableValues = {"content", "createdAt", "updatedAt", "userId", "postId"})
public class CommentOutDTO {
    private String content;

    private String createdAt;

    private String updatedAt;

    private String userFullName;

    private String postTitle;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
}
