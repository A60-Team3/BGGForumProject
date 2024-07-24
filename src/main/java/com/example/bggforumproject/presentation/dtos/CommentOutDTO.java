package com.example.bggforumproject.presentation.dtos;

import java.time.LocalDateTime;

public class CommentOutDTO {
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private long userId;

    private long postId;
}
