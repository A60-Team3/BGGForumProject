package com.example.bggforumproject.presentation.controllers;


import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.security.caseTwo.LoginDTO;
import com.example.bggforumproject.security.caseTwo.RegistrationDTO;
import com.example.bggforumproject.security.caseTwo.ResponseDTO;
import com.example.bggforumproject.service.AuthenticationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationControllerTwo {

    private final AuthenticationService authenticationService;

    public AuthenticationControllerTwo(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public User addUser(@RequestBody RegistrationDTO registrationDTO) {
        return authenticationService.registerUser(registrationDTO.firstName(),
                registrationDTO.lastName(),
                registrationDTO.email(),
                registrationDTO.password(),
                registrationDTO.username());
    }

    @PostMapping("/login")
    public ResponseDTO loginUser(@RequestBody LoginDTO body) {
        return authenticationService.loginUser(body.username(), body.password());
    }
}
