package com.example.bggforumproject.presentation.controllers;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.Reaction;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.dtos.CommentDTO;
import com.example.bggforumproject.presentation.dtos.PostCreateDTO;
import com.example.bggforumproject.presentation.dtos.PostUpdateDTO;
import com.example.bggforumproject.presentation.dtos.ReactionDTO;
import com.example.bggforumproject.presentation.exceptions.AuthorizationException;
import com.example.bggforumproject.presentation.exceptions.EntityDuplicateException;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.service.CommentService;
import com.example.bggforumproject.service.PostService;
import com.example.bggforumproject.service.ReactionService;
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
    private final CommentService commentService;
    private final ReactionService reactionService;
    private final UserService userService;
    private final ModelMapper mapper;

    public PostController(PostService postService, CommentService commentService, ReactionService reactionService, UserService userService, ModelMapper mapper) {
        this.postService = postService;
        this.commentService = commentService;
        this.reactionService = reactionService;
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

    @GetMapping("/{id}/comments")
    public List<Comment> getComments(@PathVariable long id) {
        return commentService.getCommentsForPost(id);
    }

    @GetMapping("/{id}/reactions")
    public List<Reaction> getReactions(@PathVariable long id) {
        return reactionService.getAll(id);
    }

    @PostMapping
    public Post create(@Valid @RequestBody PostCreateDTO postDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            User currentUser = userService.get(authentication.getName());
            Post post = mapper.map(postDto, Post.class);
            postService.create(post, currentUser);
            return post;
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{id}/comments")
    public Comment createComment(@PathVariable long id, @Valid @RequestBody CommentDTO commentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Post post = postService.get(id);
        Comment comment = mapper.map(commentDto, Comment.class);
        comment.setPostId(post);
        commentService.create(comment, currentUser);
        return comment;
    }

    @PostMapping("/{id}/reactions")
    public Reaction createReaction(@PathVariable long id, @Valid @RequestBody ReactionDTO reactionDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            User currentUser = userService.get(authentication.getName());
            Post post = postService.get(id);
            Reaction reaction = mapper.map(reactionDto, Reaction.class);
            reaction.setPostId(post);
            reactionService.create(reaction, currentUser, post);
            return reaction;
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable long id, @Valid @RequestBody PostUpdateDTO updateDTO) {
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

    @PutMapping("/{postId}/comments/{commentId}")
    public Comment updateComment(@PathVariable long postId,
                                 @PathVariable long commentId,
                                 @Valid @RequestBody CommentDTO commentDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
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
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }

    }

    @PutMapping("/{postId}/reactions/{reactionId}")
    public Reaction update(@PathVariable long postId,
                           @PathVariable long reactionId,
                           @Valid @RequestBody ReactionDTO reactionDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {

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
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }


    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
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

    @DeleteMapping("/{postId}/comments/{commentId}")
    public void deleteComment(@PathVariable long postId, @PathVariable long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            User currentUser = userService.get(authentication.getName());
            Comment repoComment = commentService.get(commentId);

            if (repoComment.getPostId().getId() != postId) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment does not belong to the specified post");
            }

            commentService.delete(commentId, currentUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/{postId}/reactions/{reactionId}")
    public void delete(@PathVariable long postId, @PathVariable long reactionId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {

            User currentUser = userService.get(authentication.getName());
            Reaction repoReaction = reactionService.get(reactionId);
            Post post = repoReaction.getPostId();
            if (post.getId() != postId) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reaction does not belong to the specified post");
            }

            reactionService.delete(reactionId, currentUser);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
