package com.example.bggforumproject.controllers;

import com.example.bggforumproject.dtos.CommentDTO;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.CommentService;
import com.example.bggforumproject.service.contacts.PostService;
import com.example.bggforumproject.service.contacts.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/BGGForum/posts")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    private final ModelMapper mapper;

    public CommentController(CommentService commentService, PostService postService, UserService userService, ModelMapper mapper) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}/comments")
    public List<Comment> getComments(@PathVariable long id) {
        return commentService.getCommentsForPost(id);
    }

    @PostMapping("/{id}/comments")
    public Comment create(@PathVariable long id, @Valid @RequestBody CommentDTO commentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Post post = postService.get(id);
        Comment comment = mapper.map(commentDto, Comment.class);
        comment.setPostId(post);
        commentService.create(comment, currentUser);
        return comment;
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public Comment update(@PathVariable long postId,
                                 @PathVariable long commentId,
                                 @Valid @RequestBody CommentDTO commentDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Comment repoComment = commentService.get(commentId);

        if (repoComment.getPostId().getId() != postId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment does not belong to the specified post");
        }

        Comment comment = mapper.map(commentDto, Comment.class);
        comment.setId(commentId);
        comment.setPostId(repoComment.getPostId());
        comment.setUserId(repoComment.getUserId());
        comment.setCreatedAt(repoComment.getCreatedAt());

        commentService.update(comment, currentUser);

        return comment;
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public void delete(@PathVariable long postId, @PathVariable long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Comment repoComment = commentService.get(commentId);

        if (repoComment.getPostId().getId() != postId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment does not belong to the specified post");
        }

        commentService.delete(commentId, currentUser);
    }
}
