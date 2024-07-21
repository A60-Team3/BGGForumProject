package com.example.bggforumproject.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BGG_forum/auth/")
public class AuthenticationController {

//    @PostMapping("/login")
//    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
//        return new ResponseEntity<>(authenticationService.login(loginRequestDTO), HttpStatus.OK);
//    }
}
