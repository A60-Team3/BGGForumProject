package com.example.bggforumproject.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "This DTO holds the public information about posts.", allowableValues = {"title", "createdAt", "userFullName", "tags"})
public class PostAnonymousOutDTO {
    private String title;

    private String createdAt;

    private String userFullName;

    private Set<String> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userName) {
        this.userFullName = userName;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
