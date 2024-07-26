package com.example.bggforumproject.helpers;

import java.util.Optional;

public class PostFilterOptions {

    private final Optional<String> title;
    private final Optional<String> content;
    private final Optional<Long> userId;
    private final Optional<String> tags;
    private final Optional<String> created;
    private final Optional<String> updated;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public PostFilterOptions(String title, String content, Long userId,
                             String tags, String created, String updated,
                             String sortBy, String sortOrder) {
        this.title = Optional.ofNullable(title);
        this.content = Optional.ofNullable(content);
        this.userId = Optional.ofNullable(userId);
        this.tags = Optional.ofNullable(tags);
        this.created = Optional.ofNullable(created);
        this.updated = Optional.ofNullable(updated);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getContent() {
        return content;
    }

    public Optional<Long> getUserId() {
        return userId;
    }

    public Optional<String> getTags() {
        return tags;
    }

    public Optional<String> getCreated() {
        return created;
    }

    public Optional<String> getUpdated() {
        return updated;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

}
