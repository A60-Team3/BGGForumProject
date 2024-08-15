package com.example.bggforumproject.controllers.rest;

import com.example.bggforumproject.dtos.response.ApiErrorResponseDTO;
import com.example.bggforumproject.dtos.response.PostCreateDTO;
import com.example.bggforumproject.dtos.response.PostOutFullDTO;
import com.example.bggforumproject.dtos.response.PostUpdateDTO;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.PostService;
import com.example.bggforumproject.service.contacts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//@RestController
//@RequestMapping("/BGGForum/posts")
@Tag(name = "posts", description = "Post related endpoints. For logged in visitors")
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

    @Operation(summary = "List all posts",
            description = "Authentication required. Filtering possible",
            tags = {"posts"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully displayed all posts.",
                            content = @Content(schema = @Schema(implementation = PostOutFullDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Wrong inserted date filters.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            })
    @GetMapping
    public ResponseEntity<List<PostOutFullDTO>> get(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                    @Parameter(description = "Partial or full title")
                                                    @RequestParam(required = false) String title,
                                                    @Parameter(description = "Partial or full content")
                                                    @RequestParam(required = false) String content,
                                                    @Parameter(description = "Creator id")
                                                    @RequestParam(required = false) Long userId,
                                                    @Parameter(description = "Pattern - tagId1,tagId2,tagId3")
                                                    @RequestParam(required = false) List<Long> tags,
                                                    @Parameter(description = "Pattern - [</>/<=/>=/<>/=]")
                                                        @RequestParam(required = false) String createdCondition,
                                                    @Parameter(description = "Pattern - YYYY-MM-DD HH:mm:ss")
                                                    @RequestParam(required = false) LocalDateTime created,
                                                    @Parameter(description = "Pattern - [</>/<=/>=/<>/=]")
                                                        @RequestParam(required = false) String updatedCondition,
                                                    @Parameter(description = "Pattern - YYYY-MM-DD HH:mm:ss")
                                                    @RequestParam(required = false) LocalDateTime updated,
                                                    @Parameter(description = "Options - all field names and (year/month/day)Created/Updated")
                                                    @RequestParam(required = false) String sortBy,
                                                    @RequestParam(required = false) String sortOrder
    ) {

        PostFilterOptions postFilterOptions =
                new PostFilterOptions(title, content, userId, tags,
                        createdCondition, created, updatedCondition, updated, sortBy, sortOrder);

        return ResponseEntity.ok(postService.get(postFilterOptions, pageIndex, pageSize)
                .stream()
                .map(post -> mapper.map(post, PostOutFullDTO.class))
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Find post by ID.",
            tags = {"posts"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post found successfully.",
                            content = @Content(schema = @Schema(implementation = PostOutFullDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post with such id doesnt exist.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("/{postId}")
    public ResponseEntity<PostOutFullDTO> get(@Parameter(description = "Wanted post id", required = true) @PathVariable long postId) {

        PostOutFullDTO postOutFullDTO = mapper.map(postService.get(postId), PostOutFullDTO.class);
        return ResponseEntity.ok(postOutFullDTO);
    }

    @Operation(summary = "Create post",
            description = "Create post by the logged user",
            tags = {"posts"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Title and content for creating a new post",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PostCreateDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Post successfully created.",
                            content = @Content(schema = @Schema(implementation = PostOutFullDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Post with same title exists",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PostMapping
    public ResponseEntity<PostOutFullDTO> create(@Parameter(description = "Post title and content", required = true)
                                                 @Valid @RequestBody PostCreateDTO postDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        Post post = mapper.map(postDto, Post.class);

        Post created = postService.create(post, currentUser);
        PostOutFullDTO postOutFullDTO = mapper.map(created, PostOutFullDTO.class);

        return new ResponseEntity<>(postOutFullDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Update post's info",
            description = "Can change only own posts.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Post details for updating a logged user",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PostUpdateDTO.class))
            ),
            tags = {"posts"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post updated.",
                            content = @Content(schema = @Schema(implementation = PostOutFullDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Modifying other user's post",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post with such id doesnt exist.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Post with same title exists",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PutMapping("/{postId}")
    public ResponseEntity<PostOutFullDTO> update(@Parameter(description = "Target post id", required = true)
                                                 @PathVariable long postId,
                                                 @Valid @RequestBody PostUpdateDTO updateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        Post post = mapper.map(updateDTO, Post.class);

        Post updated = postService.update(postId, post, currentUser);

        return ResponseEntity.ok(mapper.map(updated, PostOutFullDTO.class));
    }

    @Operation(summary = "Delete post.",
            description = "Creator or with admin rights",
            tags = {"posts"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted the post."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Admin rights or creator only", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            })
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(@Parameter(description = "Post to delete ID", required = true)
                                    @PathVariable long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        postService.delete(postId, currentUser);

        return ResponseEntity.ok("Post successfully deleted");
    }
}
