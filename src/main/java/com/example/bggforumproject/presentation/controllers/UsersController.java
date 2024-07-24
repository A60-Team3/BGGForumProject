package com.example.bggforumproject.presentation.controllers;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.dtos.CommentOutDTO;
import com.example.bggforumproject.presentation.dtos.PostOutFullDTO;
import com.example.bggforumproject.presentation.dtos.UserOutDTO;
import com.example.bggforumproject.presentation.helpers.CommentFilterOptions;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/BGGForum/users")
@CrossOrigin("*")
public class UsersController {

    private final UserService userService;
    private final ModelMapper mapper;


    public UsersController(UserService userService, ModelMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

//    @GetMapping("/")
//    public ResponseEntity<List<UserOutDTO>> getAll() {
//
//        List<User> users = userService
//                .getAll(new UserFilterOptions(
//                                null, null, null,
//                                null, null, null,
//                                null, null, null,
//                                null, null
//                        )
//                );
//
//        List<UserOutDTO> userOutDTOS = users.stream()
//                .map(user -> mapper.map(user, UserOutDTO.class))
//                .toList();
//        return ResponseEntity.ok(userOutDTOS);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOutDTO> getById(@PathVariable long id) {

        UserOutDTO dto = mapper.map(userService.get(id), UserOutDTO.class);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostOutFullDTO>> getUserPosts(@RequestParam(required = false) String title,
                                                             @RequestParam(required = false) String content,
                                                             @RequestParam(required = false) String tags,
                                                             @RequestParam(required = false) String created,
                                                             @RequestParam(required = false) String updated,
                                                             @RequestParam(required = false) String sortBy,
                                                             @RequestParam(required = false) String sortOrder,
                                                             @PathVariable long id) {


        PostFilterOptions postFilterOptions =
                new PostFilterOptions(title, content, id, tags, created, updated, sortBy, sortOrder);

        List<Post> filteredPosts = userService.getSpecificUserPosts(postFilterOptions);

        List<PostOutFullDTO> postOutFullDTOS = filteredPosts.stream()
                .map(post -> mapper.map(post, PostOutFullDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(postOutFullDTOS);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentOutDTO>> getUserComments(@RequestParam(required = false) String content,
                                                               @RequestParam(required = false) Long commentedTo,
                                                               @RequestParam(required = false) String created,
                                                               @RequestParam(required = false) String updated,
                                                               @RequestParam(required = false) String sortBy,
                                                               @RequestParam(required = false) String sortOrder,
                                                               @PathVariable long id) {

        CommentFilterOptions commentFilterOptions =
                new CommentFilterOptions(content, created, updated, id, commentedTo, sortBy, sortOrder);

        List<Comment> filteredComments = userService.getSpecificUserComments(commentFilterOptions);

        List<CommentOutDTO> commentOutDTOS = filteredComments.stream()
                .map(comment -> mapper.map(comment, CommentOutDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentOutDTOS);
    }
}
