package com.example.bggforumproject.presentation.controllers;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.dtos.UserOutDTO;
import com.example.bggforumproject.presentation.helpers.UserFilterOptions;
import com.example.bggforumproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/BGGForum/admin")
public class AdminController {
    private final UserService userService;
    private final ModelMapper mapper;

    public AdminController(UserService userService, ModelMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserOutDTO>> getAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String registered,
            @RequestParam(required = false) String updated,
            @RequestParam(required = false) Boolean isBlocked,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) String authority,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder
    ) {
        UserFilterOptions userFilterOptions =
                new UserFilterOptions(
                        firstName, lastName, email, username,
                        registered, updated, isBlocked, isDeleted,
                        authority, sortBy, sortOrder
                );

        List<User> users = userService.getAll(userFilterOptions);
        List<UserOutDTO> userOutDTOS = users.stream()
                .map(user -> mapper.map(user, UserOutDTO.class))
                .toList();

        return ResponseEntity.ok(userOutDTOS);
    }
}
