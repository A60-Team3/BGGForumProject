package com.example.bggforumproject.persistance.models;

import com.example.bggforumproject.persistance.models.base.BaseEntity;

import java.time.LocalDateTime;

public class Post extends BaseEntity {

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private User userId;

    public Post() {
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }
}
