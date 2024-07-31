package com.example.bggforumproject.controllers;

import com.example.bggforumproject.dtos.ApiErrorResponseDTO;
import com.example.bggforumproject.dtos.CommentDTO;
import com.example.bggforumproject.dtos.PostAllReactionsDTO;
import com.example.bggforumproject.dtos.ReactionOutDTO;
import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.ReactionType;
import com.example.bggforumproject.service.contacts.ReactionService;
import com.example.bggforumproject.service.contacts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BGGForum/posts")
@Tag(name = "reactions", description = "Reactions related endpoints. Authentication required.")
public class ReactionController {

    private final ReactionService reactionService;
    private final UserService userService;
    private final ModelMapper mapper;

    public ReactionController(ReactionService reactionService, UserService userService, ModelMapper mapper) {
        this.reactionService = reactionService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Operation(summary = "List all reactions for a specific post.",
            tags = {"reactions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully list the reactions of a post.",
                            content = @Content(schema = @Schema(implementation = PostAllReactionsDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post with such id doesnt exist.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("/{postId}/reactions")
    public ResponseEntity<PostAllReactionsDTO> get(@Parameter(description = "Target post id", required = true)
                                                   @PathVariable long postId) {
        List<Reaction> reactions = reactionService.getAll(postId);

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

    @Operation(summary = "Add reaction",
            description = "Add reaction to a specific post",
            tags = {"reactions"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Reaction successfully added.",
                            content = @Content(schema = @Schema(implementation = CommentDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post with such id doesnt exist.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "The user already reacted to this post.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PostMapping("/{postId}/reactions")
    public ResponseEntity<?> create(@Parameter(description = "Target post id", required = true)
                                    @PathVariable long postId,
                                    @Parameter(description = "Reaction type ENUM", required = true)
                                    @RequestParam ReactionType reactionType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        Reaction reaction = new Reaction();
        reaction.setReactionType(reactionType);

        reactionService.create(reaction, currentUser, postId);

        return new ResponseEntity<>("Post reacted to successfully.", HttpStatus.CREATED);
    }

    @Operation(summary = "Change reaction",
            description = "Change your reaction to a specific post",
            tags = {"reactions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reaction successfully updated.",
                            content = @Content(schema = @Schema(implementation = PostAllReactionsDTO.class))),
                    @ApiResponse(responseCode = "400", description = "No such post-reaction combination.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Reaction creator only",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post/Tag with such id doesnt exist.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "The user already reacted to this post.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PutMapping("/{postId}/reactions/{reactionId}")
    public ResponseEntity<PostAllReactionsDTO> update(@Parameter(description = "Target post id", required = true)
                                                      @PathVariable long postId,
                                                      @Parameter(description = "Old reaction id", required = true)
                                                      @PathVariable long reactionId,
                                                      @Parameter(description = "New reaction type ENUM", required = true)
                                                      @RequestParam ReactionType reactionType) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        Reaction updatedReaction = reactionService.update(reactionId, currentUser, postId, reactionType);

        ReactionOutDTO reactionOutDTO = mapper.map(updatedReaction, ReactionOutDTO.class);

        PostAllReactionsDTO result =
                new PostAllReactionsDTO(updatedReaction.getPostId().getTitle(),
                        String.format("%s %s",
                                updatedReaction.getUserId().getFirstName(),
                                updatedReaction.getUserId().getLastName()),
                        List.of(reactionOutDTO)
                );
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Remove reaction",
            description = "Remove your reaction from a specific post",
            tags = {"reactions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reaction successfully deleted.",
                            content = @Content(schema = @Schema(implementation = PostAllReactionsDTO.class))),
                    @ApiResponse(responseCode = "400", description = "No such post-reaction combination.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Reaction creator only",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post/Tag with such id doesnt exist.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @DeleteMapping("/{postId}/reactions/{reactionId}")
    public ResponseEntity<?> delete(@Parameter(description = "Target post id", required = true)
                                    @PathVariable long postId,
                                    @Parameter(description = "Reaction id to delete", required = true)
                                    @PathVariable long reactionId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        reactionService.delete(reactionId, currentUser, postId);

        return ResponseEntity.ok("Reaction removed from post successfully");

    }

}
