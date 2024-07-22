package com.example.bggforumproject.presentation.helpers;

import java.util.Optional;

public class PostFilterOptions {

    private Optional<String> title;
    private Optional<Integer> tagId;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public PostFilterOptions(String title, Integer tagId, String sortBy, String sortOrder){
        this.title = Optional.ofNullable(title);
        this.tagId = Optional.ofNullable(tagId);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<Integer> getTagId() {
        return tagId;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
