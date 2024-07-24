package com.example.bggforumproject.service;


import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.security.caseOne.LoginUserDTO;
import com.example.bggforumproject.security.caseOne.RegisterUserDTO;
import com.example.bggforumproject.presentation.dtos.RegistrationDTO;
import com.example.bggforumproject.presentation.dtos.ResponseDTO;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    ResponseDTO loginUser(Authentication auth);

    User registerUser(RegisterUserDTO user);

    ResponseDTO registerUser(RegistrationDTO dto);

    User loginUser(LoginUserDTO input);
}
