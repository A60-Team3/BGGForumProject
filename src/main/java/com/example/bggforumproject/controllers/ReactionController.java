package com.example.bggforumproject.controllers;

import com.example.bggforumproject.dtos.PostAllReactionsDTO;
import com.example.bggforumproject.dtos.ReactionDTO;
import com.example.bggforumproject.dtos.ReactionOutDTO;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.PostService;
import com.example.bggforumproject.service.contacts.ReactionService;
import com.example.bggforumproject.service.contacts.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/BGGForum/posts")
public class ReactionController {

    private final ReactionService reactionService;
    private final PostService postService;
    private final UserService userService;
    private final ModelMapper mapper;

    public ReactionController(ReactionService reactionService, PostService postService, UserService userService, ModelMapper mapper) {
        this.reactionService = reactionService;
        this.postService = postService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}/reactions")
    public ResponseEntity<PostAllReactionsDTO> get(@PathVariable long id) {
        List<Reaction> reactions = reactionService.getAll(id);

        String title = reactions.get(0).getPostId().getTitle();
        User postMaker = reactions.get(0).getPostId().getUserId();

        List<ReactionOutDTO> list = reactions.stream()
                .map(reaction -> mapper.map(reaction, ReactionOutDTO.class))
                .toList();

        PostAllReactionsDTO result =
                new PostAllReactionsDTO(title,
                        String.format("%s %s", postMaker.getFirstName(), postMaker.getLastName()),
                        list
                );

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/reactions")
    public Reaction create(@PathVariable long id, @Valid @RequestBody ReactionDTO reactionDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Post post = postService.get(id);
        Reaction reaction = mapper.map(reactionDto, Reaction.class);
        reaction.setPostId(post);
        reactionService.create(reaction, currentUser, post);
        return reaction;
    }

    @PutMapping("/{postId}/reactions/{reactionId}")
    public Reaction update(@PathVariable long postId,
                           @PathVariable long reactionId,
                           @Valid @RequestBody ReactionDTO reactionDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Reaction repoReaction = reactionService.get(reactionId);
        Post post = repoReaction.getPostId();
        if (post.getId() != postId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reaction does not belong to the specified post");
        }

        Reaction reaction = mapper.map(reactionDto, Reaction.class);
        reaction.setId(reactionId);
        reaction.setPostId(repoReaction.getPostId());
        reaction.setUserId(repoReaction.getUserId());

        reactionService.update(reaction, currentUser, post);
        return reaction;
    }

    @DeleteMapping("/{postId}/reactions/{reactionId}")
    public ResponseEntity<?> delete(@PathVariable long postId, @PathVariable long reactionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Reaction repoReaction = reactionService.get(reactionId);
        Post post = repoReaction.getPostId();
        if (post.getId() != postId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reaction does not belong to the specified post");
        }

        reactionService.delete(reactionId, currentUser);

        return ResponseEntity.ok("Tag removed from post successfully");

    }

}
