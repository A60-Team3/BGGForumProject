package com.example.bggforumproject.dtos;

import java.util.List;

public record UnknownOutDTO(long userCount, long postCount, List<PostAnonymousOutDTO> postOutFullDTOS) {
}
