package com.example.bggforumproject.security.caseTwo;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateJwt(Authentication auth);
}
