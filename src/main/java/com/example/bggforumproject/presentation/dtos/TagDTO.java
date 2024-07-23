package com.example.bggforumproject.presentation.dtos;

import jakarta.validation.constraints.NotNull;

public class TagDTO {

    @NotNull(message = "Tag name cannot be empty!")
    private String name;

    public TagDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
