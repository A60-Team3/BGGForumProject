package com.example.bggforumproject.service.contacts;


import com.example.bggforumproject.models.User;
import com.example.bggforumproject.dtos.ResponseDTO;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    ResponseDTO loginUser(Authentication auth);

    User registerUser(User user);
}
