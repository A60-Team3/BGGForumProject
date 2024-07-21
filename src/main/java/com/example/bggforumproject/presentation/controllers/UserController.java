package com.example.bggforumproject.presentation.controllers;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.helpers.AuthenticationHelper;
import com.example.bggforumproject.presentation.helpers.UserMapper;
import com.example.bggforumproject.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BGG_forum/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper mapper;

    public UserController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper mapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.mapper = mapper;
    }

    @GetMapping
    public List<User> getAll(){
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@RequestHeader HttpHeaders headers, @PathVariable long id){
        authenticationHelper.tryGetUser(headers);
        return userService.get(id);
    }


}
