package com.example.bggforumproject.service;


import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.security.caseOne.LoginUserDTO;
import com.example.bggforumproject.security.caseOne.RegisterUserDTO;
import com.example.bggforumproject.security.caseTwo.ResponseDTO;

public interface AuthenticationService {
    User registerUser(RegisterUserDTO user);
    ResponseDTO loginUser(String username, String password);

    User loginUser(LoginUserDTO input);

    User registerUser(String firstName, String lastName, String email, String password, String username);
}
