package com.example.bggforumproject.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record FilterDto(String title, String content,
                        Long userId, List<Long> tags,
                        String createCondition, LocalDateTime created,
                        String updateCondition, LocalDateTime updated,
                        String sortBy, String sortOrder) {
}

