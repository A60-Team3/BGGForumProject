package com.example.bggforumproject.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "This DTO holds possible tag name.",
        allowableValues = {"name"})
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
