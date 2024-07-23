package com.example.bggforumproject.presentation.controllers;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.dtos.PostDTO;
import com.example.bggforumproject.presentation.exceptions.AuthorizationException;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.presentation.helpers.PostMapper;
import com.example.bggforumproject.service.PostService;
import com.example.bggforumproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/BGGForum/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final PostMapper postMapper;

    public PostController(PostService postService, UserService userService, PostMapper postMapper) {
        this.postService = postService;
        this.userService = userService;
        this.postMapper = postMapper;
    }

    @GetMapping
    public List<Post> get(@RequestParam(required = false) String title,
                          @RequestParam(required = false) Integer tagId,
                          @RequestParam(required = false) String sortBy,
                          @RequestParam(required = false) String sortOrder) {

        PostFilterOptions postFilterOptions = new PostFilterOptions(title, tagId, sortBy, sortOrder);
        return postService.get(postFilterOptions);
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
    public Post create(@Valid @RequestBody PostDTO postDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Post post = postMapper.fromDto(postDto);
        postService.create(post, currentUser);
        return post;
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable int id, @Valid @RequestBody PostDTO postDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            User currentUser = userService.get(authentication.getName());
            Post post = postMapper.fromDto(id, postDto);
            postService.update(post, currentUser);
            return post;
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            User currentUser = userService.get(authentication.getName());
            postService.delete(id, currentUser);
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

}
