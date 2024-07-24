package com.example.bggforumproject.presentation.dtos;


import java.time.LocalDateTime;
import java.util.Set;

public class PostOutFullDTO {
    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private long userId;

    private Set<Long> tagId;
}
