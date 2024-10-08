package com.example.bggforumproject.dtos.response;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "This DTO holds post info available to authenticated users.",
        allowableValues = {"title", "content", "createdAt", "updatedAt", "publisherName", "tags"})
public class PostOutFullDTO {
    private String title;

    private String content;

    private String createdAt;

    private String updatedAt;

    private String publisherName;

    private Set<String> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
