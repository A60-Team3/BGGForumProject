package com.example.bggforumproject.presentation.helpers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
