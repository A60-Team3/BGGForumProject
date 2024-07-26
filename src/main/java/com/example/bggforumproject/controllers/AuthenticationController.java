package com.example.bggforumproject.controllers;


import com.example.bggforumproject.dtos.LoginDTO;
import com.example.bggforumproject.dtos.RegistrationDTO;
import com.example.bggforumproject.dtos.ResponseDTO;
import com.example.bggforumproject.exceptions.CustomAuthenticationException;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.AuthenticationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper mapper;


    public AuthenticationController(AuthenticationService authenticationService, AuthenticationManager authenticationManager, ModelMapper mapper) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    public ResponseDTO registerUser(@Valid @RequestBody RegistrationDTO registrationDTO) {

        User user = mapper.map(registrationDTO, User.class);

        return authenticationService.registerUser(user);
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
