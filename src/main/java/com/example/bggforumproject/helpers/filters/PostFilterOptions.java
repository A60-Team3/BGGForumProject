package com.example.bggforumproject.helpers.filters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PostFilterOptions {

    private final Optional<String> title;
    private final Optional<String> content;
    private final Optional<Long> userId;
    private final Optional<List<Long>> tags;
    private final Optional<List<Long>> postIds;
    private final Optional<String> createdCondition;
    private final Optional<LocalDateTime> created;
    private final Optional<String> updatedCondition;
    private final Optional<LocalDateTime> updated;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public PostFilterOptions(String title, String content, Long userId,
                             List<Long> tags, List<Long> postIds, String createdCondition, LocalDateTime created, String updatedCondition, LocalDateTime updated,
                             String sortBy, String sortOrder) {
        this.title = Optional.ofNullable(title);
        this.content = Optional.ofNullable(content);
        this.userId = Optional.ofNullable(userId);
        this.tags = Optional.ofNullable(tags);
        this.postIds = Optional.ofNullable(postIds);
        this.createdCondition = Optional.ofNullable(createdCondition);
        this.created = Optional.ofNullable(created);
        this.updatedCondition = Optional.ofNullable(updatedCondition);
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

    public Optional<List<Long>> getTags() {
        return tags;
    }

    public Optional<LocalDateTime> getCreated() {
        return created;
    }

    public Optional<LocalDateTime> getUpdated() {
        return updated;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public Optional<String> getCreatedCondition() {
        return createdCondition;
    }

    public Optional<String> getUpdatedCondition() {
        return updatedCondition;
    }

    public Optional<List<Long>> getPostIds() {
        return postIds;
    }
}
