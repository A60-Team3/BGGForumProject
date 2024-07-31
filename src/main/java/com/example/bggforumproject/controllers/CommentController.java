package com.example.bggforumproject.controllers;

import com.example.bggforumproject.dtos.ApiErrorResponseDTO;
import com.example.bggforumproject.dtos.CommentDTO;
import com.example.bggforumproject.dtos.CommentOutDTO;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.CommentService;
import com.example.bggforumproject.service.contacts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BGGForum/posts")
@Tag(name = "comments", description = "Comment related endpoints. Authentication required.")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final ModelMapper mapper;

    public CommentController(CommentService commentService, UserService userService, ModelMapper mapper) {
        this.commentService = commentService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Operation(summary = "List all comments for a certain post",
            description = "Filtration possible.",
            tags = {"comments"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully returned all comments.", content = @Content(schema = @Schema(implementation = CommentOutDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Wrong inserted date filters.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentOutDTO>> getComments(@Parameter(description = "Target Post ID", required = true)
                                                           @PathVariable long postId,
                                                           @Parameter(description = "Partial or full words")
                                                           @RequestParam(required = false) String content,
                                                           @Parameter(description = "Creator id")
                                                           @RequestParam(required = false) Long userID,
                                                           @Parameter(description = "Pattern - [</>/<=/>=/<>/=],YYYY-MM-DD HH:mm:ss")
                                                           @RequestParam(required = false) String created,
                                                           @Parameter(description = "Pattern - [</>/<=/>=/<>/=],YYYY-MM-DD HH:mm:ss")
                                                           @RequestParam(required = false) String updated,
                                                           @Parameter(description = "Options - all field names and (year/month/day)Created/Updated")
                                                           @RequestParam(required = false) String sortBy,
                                                           @RequestParam(required = false) String sortOrder) {

        CommentFilterOptions commentFilterOptions =
                new CommentFilterOptions(content, created, updated, userID, postId, sortBy, sortOrder);

        List<Comment> commentsForPost = commentService.getCommentsForPost(postId, commentFilterOptions);

        List<CommentOutDTO> commentOutDTOS = commentsForPost.stream()
                .map(comment -> mapper.map(comment, CommentOutDTO.class))
                .toList();

        return ResponseEntity.ok(commentOutDTOS);
    }

    @Operation(summary = "Create comment",
            description = "Add comment to a specific post",
            tags = {"comments"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Comment details for creating a new comment",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CommentDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Comment successfully created.", content = @Content(schema = @Schema(implementation = CommentDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentOutDTO> create(
            @Parameter(description = "Commented post id", required = true)
            @PathVariable long postId,
            @Parameter(description = "Comment creation request body")
            @Valid @RequestBody CommentDTO commentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        Comment comment = mapper.map(commentDto, Comment.class);

        Comment created = commentService.create(postId, comment, currentUser);
        CommentOutDTO commentOutDTO = mapper.map(created, CommentOutDTO.class);

        return new ResponseEntity<>(commentOutDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Update comment",
            description = "Update a comment you have made to a specific post. Returns the updated comment",
            tags = {"comments"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated a comment.", content = @Content(schema = @Schema(implementation = CommentOutDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Post-comment mismatch", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Modifying other user info", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post/Comment with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Content not updated.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentOutDTO> update(
            @Parameter(description = "Commented post id", required = true)
            @PathVariable long postId,
            @Parameter(description = "Updated comment id", required = true)
            @PathVariable long commentId,
            @Parameter(description = "Updated content", required = true)
            @Size(min = 32, max = 8192, message = "Content should be between 32 and 8192 symbols!")
            @RequestParam String newContent) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        Comment updated = commentService.update(commentId, postId, newContent, currentUser);

        return ResponseEntity.ok(mapper.map(updated, CommentOutDTO.class));
    }

    @Operation(summary = "Delete comment",
            description = "Delete a comment if you are the owner or have admin rights.",
            tags = {"comments"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted a comment."),
                    @ApiResponse(responseCode = "400", description = "Post-comment mismatch", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Not the creator or an admin", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post/Comment with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> delete(@PathVariable long postId, @PathVariable long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());


        commentService.delete(commentId, postId, currentUser);

        return ResponseEntity.ok("Successfully deleted a comment.");
    }
}
