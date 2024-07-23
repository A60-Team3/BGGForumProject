package com.example.bggforumproject.presentation.controllers;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.dtos.UserOutDTO;
import com.example.bggforumproject.presentation.helpers.AuthenticationHelper;
import com.example.bggforumproject.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/BGGForum/users")
@CrossOrigin("*")
public class UsersController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    public UsersController(UserService userService, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAll(){
        List<User> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOutDTO> getById(@PathVariable long id){

        UserOutDTO dto = userService.get(id);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<Post>> getUserPosts(@RequestParam(required = false) String title,
                                                   @RequestParam(required = false) String content,
                                                   @RequestParam(required = false) Integer userId,
                                                   @RequestParam(required = false) String tags,
                                                   @RequestParam(required = false) LocalDateTime created,
                                                   @RequestParam(required = false) LocalDateTime updated
                                                   ){
        return ResponseEntity.ok(new ArrayList<>());
    }
}
