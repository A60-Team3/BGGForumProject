package com.example.bggforumproject.controllers;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.dtos.PostAnonymousOutDTO;
import com.example.bggforumproject.dtos.UnknownOutDTO;
import com.example.bggforumproject.helpers.PostFilterOptions;
import com.example.bggforumproject.service.contacts.AnonymousUserService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/BGGForum")
public class AnonymousUserController {
    private final AnonymousUserService anonymousUserService;
    private final ModelMapper mapper;

    public AnonymousUserController(AnonymousUserService anonymousUserService, ModelMapper mapper) {
        this.anonymousUserService = anonymousUserService;
        this.mapper = mapper;
    }

    @GetMapping("/main")
    public UnknownOutDTO mainPage(@RequestParam(required = false) String title,
                                  @RequestParam(required = false) String created,
                                  @RequestParam(required = false) String tags,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder) {

        PostFilterOptions postFilterOptions =
                new PostFilterOptions(
                        title, null, null, tags, created, null, sortBy, sortOrder
                );
        long users = anonymousUserService.countUsers();
        long posts = anonymousUserService.countPosts();

        List<Post> filteredPosts = anonymousUserService.getAllPosts(postFilterOptions);

        List<PostAnonymousOutDTO> postOutFullDTOS = filteredPosts.stream()
                .map(post -> mapper.map(post, PostAnonymousOutDTO.class))
                .toList();

        return new UnknownOutDTO(users, posts, postOutFullDTOS);
    }
}
