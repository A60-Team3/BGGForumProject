package com.example.bggforumproject.presentation.dtos;

import java.util.List;

public record UnknownOutDTO(long userCount, long postCount, List<PostAnonymousOutDTO> postOutFullDTOS) {
}
