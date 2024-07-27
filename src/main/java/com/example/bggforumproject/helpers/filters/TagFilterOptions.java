package com.example.bggforumproject.helpers.filters;

import java.util.Optional;

public class TagFilterOptions {
    private final Optional<Long> tagId;
    private final Optional<String> name;
    private final Optional<String> postIds;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public TagFilterOptions(Long tagId, String name,
                            String postIds, String sortBy,
                            String sortOrder) {
        this.tagId = Optional.ofNullable(tagId);
        this.name = Optional.ofNullable(name);
        this.postIds = Optional.ofNullable(postIds);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<Long> getTagId() {
        return tagId;
    }

    public Optional<String> getName() {
        return name;
    }

    public Optional<String> getPostIds() {
        return postIds;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
