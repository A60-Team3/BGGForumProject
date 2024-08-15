package com.example.bggforumproject.dtos.request;

import com.example.bggforumproject.dtos.response.PostAnonymousOutDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "This DTO holds the public information about the forum.", allowableValues = {"userCount", "postCount", "posts"})
public record UnknownOutDTO(long userCount, long postCount, List<PostAnonymousOutDTO> posts) {
}
