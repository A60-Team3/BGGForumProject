package com.example.bggforumproject.helpers;

import java.util.Optional;

public class CommentFilterOptions {

    private final Optional<String> content;
    private final Optional<String> created;
    private final Optional<String> updated;
    private final Optional<Long> createdBy;
    private final Optional<Long> commentedTo;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public CommentFilterOptions(String content, String created, String updated,
                                Long createdBy, Long commentedTo,
                                String sortBy, String sortOrder) {
        this.created = Optional.ofNullable(created);
        this.updated = Optional.ofNullable(updated);
        this.createdBy = Optional.ofNullable(createdBy);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
        this.content = Optional.ofNullable(content);
        this.commentedTo = Optional.ofNullable(commentedTo);
    }

    public Optional<String> getContent() {
        return content;
    }

    public Optional<String> getCreated() {
        return created;
    }

    public Optional<String> getUpdated() {
        return updated;
    }

    public Optional<Long> getCreatedBy() {
        return createdBy;
    }

    public Optional<Long> getCommentedTo() {
        return commentedTo;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
