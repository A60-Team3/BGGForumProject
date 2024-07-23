package com.example.bggforumproject.presentation.controllers;

import com.example.bggforumproject.presentation.dtos.UnknownOutDTO;
import com.example.bggforumproject.service.AnonymousUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BGGForum")
public class AnonymousUserController {
    private final AnonymousUserService anonymousUserService;

    public AnonymousUserController(AnonymousUserService anonymousUserService) {
        this.anonymousUserService = anonymousUserService;
    }

    @GetMapping
    public UnknownOutDTO mainPage(){
        long users = anonymousUserService.countUsers();
        long posts = anonymousUserService.countPosts();

        return new UnknownOutDTO(users, posts);
    }
}
