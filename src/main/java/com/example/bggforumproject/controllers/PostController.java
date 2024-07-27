package com.example.bggforumproject.controllers;

import com.example.bggforumproject.dtos.*;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.models.*;
import com.example.bggforumproject.service.contacts.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/BGGForum/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final ModelMapper mapper;

    public PostController(PostService postService,
                          UserService userService,
                          ModelMapper mapper) {
        this.postService = postService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PostOutFullDTO> get(@RequestParam(required = false) String title,
                                    @RequestParam(required = false) String content,
                                    @RequestParam(required = false) Long userId,
                                    @RequestParam(required = false) String tags,
                                    @RequestParam(required = false) String created,
                                    @RequestParam(required = false) String updated,
                                    @RequestParam(required = false) String sortBy,
                                    @RequestParam(required = false) String sortOrder
    ) {

        PostFilterOptions postFilterOptions =
                new PostFilterOptions(title, content, userId, tags, created, updated, sortBy, sortOrder);

        return postService.get(postFilterOptions)
                .stream()
                .map(post -> mapper.map(post, PostOutFullDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Post get(@PathVariable int id) {

        return postService.get(id);
    }

    @PostMapping
    public Post create(@Valid @RequestBody PostCreateDTO postDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Post post = mapper.map(postDto, Post.class);
        postService.create(post, currentUser);
        return post;
    }

 @PutMapping("/{id}")
    public Post update(@PathVariable long id, @Valid @RequestBody PostUpdateDTO updateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Post post = mapper.map(updateDTO, Post.class);

        post.setId(id);
        Post repoPost = postService.get(id);
        post.setUserId(repoPost.getUserId());
        post.setCreatedAt(repoPost.getCreatedAt());
        postService.update(post, currentUser);

        return post;
    }

   @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        postService.delete(id, currentUser);
    }
}
