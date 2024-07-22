package com.example.bggforumproject.presentation.helpers;

import java.time.LocalDateTime;
import java.util.Optional;

public class CommentFilterOptions {

    private Optional<LocalDateTime> createdBefore;
    private Optional<LocalDateTime> createdAfter;
    private Optional<String> createdBy;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public CommentFilterOptions(Optional<LocalDateTime> createdBefore,
                                Optional<LocalDateTime> createdAfter,
                                Optional<String> createdBy,
                                Optional<String> sortBy,
                                Optional<String> sortOrder) {
        this.createdBefore = createdBefore;
        this.createdAfter = createdAfter;
        this.createdBy = createdBy;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public Optional<LocalDateTime> getCreatedBefore() {
        return createdBefore;
    }

    public Optional<LocalDateTime> getCreatedAfter() {
        return createdAfter;
    }

    public Optional<String> getCreatedBy() {
        return createdBy;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
