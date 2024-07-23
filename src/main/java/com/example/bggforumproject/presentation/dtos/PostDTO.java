package com.example.bggforumproject.presentation.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostDTO {

    @NotNull(message = "Title cannot be empty!")
    @Size(min = 16, max = 64, message = "Title should be between 16 and 64 symbols!")
    private String title;

    @NotNull(message = "Content cannot be empty!")
    @Size(min = 32, max = 8192, message = "Content should be between 32 and 8192 symbols!")
    private String content;

    public PostDTO() {
    }

    public  String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
