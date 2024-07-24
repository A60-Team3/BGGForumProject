package com.example.bggforumproject.presentation.controllers;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.dtos.PostCreateDTO;
import com.example.bggforumproject.presentation.dtos.PostUpdateDTO;
import com.example.bggforumproject.presentation.exceptions.AuthorizationException;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.service.PostService;
import com.example.bggforumproject.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/BGGForum/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final ModelMapper mapper;

    public PostController(PostService postService, UserService userService, ModelMapper mapper) {
        this.postService = postService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PostCreateDTO> get(@RequestParam(required = false) String title,
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
        return postService.get(postFilterOptions).stream().map(post -> mapper.map(post, PostCreateDTO.class)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Post get(@PathVariable int id) {
        try {
            return postService.get(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/most-commented")
    public List<Post> getMostCommented() {
        return postService.getMostCommented();
    }

    @GetMapping("/most-recently-created")
    public List<Post> getMostRecentlyCreated() {
        return postService.getMostRecentlyCreated();
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
    public Post update(@PathVariable int id, @Valid @RequestBody PostUpdateDTO updateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            User currentUser = userService.get(authentication.getName());
            Post post = mapper.map(updateDTO, Post.class);

            post.setId(id);
            Post repoPost = postService.get(id);
            post.setUserId(repoPost.getUserId());
            postService.update(post, currentUser);
            post.setCreatedAt(repoPost.getCreatedAt());

            return post;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            User currentUser = userService.get(authentication.getName());
            postService.delete(id, currentUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

}
