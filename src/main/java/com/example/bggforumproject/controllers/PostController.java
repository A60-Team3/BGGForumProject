package com.example.bggforumproject.controllers;

import com.example.bggforumproject.dtos.*;
import com.example.bggforumproject.helpers.PostFilterOptions;
import com.example.bggforumproject.models.*;
import com.example.bggforumproject.service.contacts.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final TagService tagService;
    private final UserService userService;
    private final ModelMapper mapper;

    public PostController(PostService postService,
                          CommentService commentService,
                          ReactionService reactionService,
                          TagService tagService,
                          UserService userService,
                          ModelMapper mapper) {
        this.postService = postService;
        this.commentService = commentService;
        this.reactionService = reactionService;
        this.tagService = tagService;
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

    @GetMapping("/most-commented")
    public ResponseEntity<List<PostOutFullDTO>> getMostCommented() {
        List<Post> mostCommented = postService.getMostCommented();

        List<PostOutFullDTO> postOutFullDTOS = mostCommented.stream()
                .map(post -> mapper.map(post, PostOutFullDTO.class))
                .toList();

        return ResponseEntity.ok(postOutFullDTOS);
    }

    @GetMapping("/most-recently-created")
    public ResponseEntity<List<PostOutFullDTO>> getMostRecentlyCreated() {
        List<Post> mostRecentlyCreated = postService.getMostRecentlyCreated();

        List<PostOutFullDTO> postOutFullDTOS = mostRecentlyCreated.stream()
                .map(post -> mapper.map(post, PostOutFullDTO.class))
                .toList();

        return ResponseEntity.ok(postOutFullDTOS);
    }

    @GetMapping("/{id}/comments")
    public List<Comment> getComments(@PathVariable long id) {
        return commentService.getCommentsForPost(id);
    }

    @GetMapping("/{id}/reactions")
    public ResponseEntity<PostAllReactionsDTO> getReactions(@PathVariable long id) {
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

    @PostMapping("/{id}/tags")
    public Post addTagToPost(@PathVariable long id, @Valid @RequestBody TagDTO tagDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        Post post = postService.get(id);
        Tag tagToAdd = mapper.map(tagDto, Tag.class);
        tagService.addTagToPost(tagToAdd, post);

        return post;
    }

    @PostMapping
    public Post create(@Valid @RequestBody PostCreateDTO postDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Post post = mapper.map(postDto, Post.class);
        postService.create(post, currentUser);
        return post;
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

        User currentUser = userService.get(authentication.getName());
        Post post = postService.get(id);
        Reaction reaction = mapper.map(reactionDto, Reaction.class);
        reaction.setPostId(post);
        reactionService.create(reaction, currentUser, post);
        return reaction;
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

    @PutMapping("/{postId}/comments/{commentId}")
    public Comment updateComment(@PathVariable long postId,
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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        postService.delete(id, currentUser);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public void deleteComment(@PathVariable long postId, @PathVariable long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Comment repoComment = commentService.get(commentId);

        if (repoComment.getPostId().getId() != postId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment does not belong to the specified post");
        }

        commentService.delete(commentId, currentUser);
    }

    @DeleteMapping("/{postId}/reactions/{reactionId}")
    public void delete(@PathVariable long postId, @PathVariable long reactionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Reaction repoReaction = reactionService.get(reactionId);
        Post post = repoReaction.getPostId();
        if (post.getId() != postId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reaction does not belong to the specified post");
        }

        reactionService.delete(reactionId, currentUser);
    }

    @DeleteMapping("/{postId}/tags/{tagId}")
    public void deleteTagFromPost(@PathVariable long postId, @PathVariable long tagId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        Tag tag = tagService.get(tagId);
        Post post = tag.getPosts().stream().filter(p -> p.getId() == postId).findFirst().orElseThrow();
        if (post.getId() != postId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong post");
        }

        tagService.deleteTagFromPost(tagId, post, currentUser);
    }

    @DeleteMapping("/tags/{id}")
    public void deleteTag(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        tagService.delete(id, currentUser);
    }
}
