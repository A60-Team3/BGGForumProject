package com.example.bggforumproject.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Schema(description = "This DTO holds the information necessary to create new comment.",
        allowableValues = {"content"})
public class CommentDTO {

    @NotNull(message = "Content cannot be empty!")
    @Size(min = 1, max = 8192, message = "Content should be between 32 and 8192 symbols!")
    private String content;

    public CommentDTO() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
