package com.example.bggforumproject.dtos.request;

import com.example.bggforumproject.models.enums.RoleType;

import java.time.LocalDateTime;
import java.util.List;

public record FilterDto(String firstName, String lastName,
                        String email, String username,
                        Boolean isBlocked, Boolean isDeleted,
                        String phoneNumber, List<RoleType> roles,
                        String title, String content,
                        Long userId, List<Long> tags,
                        List<Long> postIds,
                        String createCondition, LocalDateTime created,
                        String updateCondition, LocalDateTime updated,
                        String sortBy, String sortOrder) {
}

