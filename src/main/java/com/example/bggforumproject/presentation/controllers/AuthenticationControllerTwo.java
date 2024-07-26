package com.example.bggforumproject.presentation.controllers;


import com.example.bggforumproject.presentation.exceptions.CustomAuthenticationException;
import com.example.bggforumproject.presentation.dtos.LoginDTO;
import com.example.bggforumproject.presentation.dtos.RegistrationDTO;
import com.example.bggforumproject.presentation.dtos.ResponseDTO;
import com.example.bggforumproject.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationControllerTwo {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationControllerTwo(AuthenticationService authenticationService, AuthenticationManager authenticationManager) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseDTO registerUser(@Valid @RequestBody RegistrationDTO registrationDTO) {
        return authenticationService.registerUser(registrationDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> loginUser(@RequestBody LoginDTO body) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.username(), body.password())
            );
            ResponseDTO dto = authenticationService.loginUser(auth);
            return ResponseEntity.ok(dto);

        } catch (AuthenticationException e) {
            throw new CustomAuthenticationException("Authentication failed!");
        }
    }

    @PostMapping("/logout")
    public void logoutUser() {
    }
}
