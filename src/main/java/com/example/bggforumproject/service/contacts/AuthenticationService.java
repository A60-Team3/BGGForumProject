package com.example.bggforumproject.service.contacts;


import com.example.bggforumproject.models.User;
import com.example.bggforumproject.security.alternative.LoginUserDTO;
import com.example.bggforumproject.security.alternative.RegisterUserDTO;
import com.example.bggforumproject.dtos.ResponseDTO;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    ResponseDTO loginUser(Authentication auth);

    ResponseDTO registerUser(User user);
}
