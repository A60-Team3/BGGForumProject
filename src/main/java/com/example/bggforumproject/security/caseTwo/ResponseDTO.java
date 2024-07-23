package com.example.bggforumproject.security.caseTwo;


import com.example.bggforumproject.persistance.models.User;

public record ResponseDTO(User user, String jwt) {}
