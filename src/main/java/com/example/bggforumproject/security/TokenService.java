package com.example.bggforumproject.security;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateJwt(Authentication auth);
}
