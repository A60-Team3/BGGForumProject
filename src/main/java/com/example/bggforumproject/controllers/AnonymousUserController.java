package com.example.bggforumproject.controllers;

import com.example.bggforumproject.dtos.PostOutFullDTO;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.dtos.PostAnonymousOutDTO;
import com.example.bggforumproject.dtos.UnknownOutDTO;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.service.contacts.AnonymousUserService;
import com.example.bggforumproject.service.contacts.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/BGGForum")
public class AnonymousUserController {
    private final AnonymousUserService anonymousUserService;
    private final PostService postService;
    private final ModelMapper mapper;

    public AnonymousUserController(AnonymousUserService anonymousUserService, PostService postService, ModelMapper mapper) {
        this.anonymousUserService = anonymousUserService;
        this.postService = postService;
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

    @GetMapping("/posts/most-commented")
    public ResponseEntity<List<PostOutFullDTO>> getMostCommented() {
        List<Post> mostCommented = postService.getMostCommented();

        List<PostOutFullDTO> postOutFullDTOS = mostCommented.stream()
                .map(post -> mapper.map(post, PostOutFullDTO.class))
                .toList();

        return ResponseEntity.ok(postOutFullDTOS);
    }

    @GetMapping("/posts/most-recently-created")
    public ResponseEntity<List<PostOutFullDTO>> getMostRecentlyCreated() {
        List<Post> mostRecentlyCreated = postService.getMostRecentlyCreated();

        List<PostOutFullDTO> postOutFullDTOS = mostRecentlyCreated.stream()
                .map(post -> mapper.map(post, PostOutFullDTO.class))
                .toList();

        return ResponseEntity.ok(postOutFullDTOS);
    }
}
