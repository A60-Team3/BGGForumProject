package com.example.bggforumproject.controllers;

import com.example.bggforumproject.dtos.*;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.helpers.filters.UserFilterOptions;
import com.example.bggforumproject.service.contacts.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/BGGForum/users")
//@Tag(name = "users", description = "User related endpoints. For logged in visitors")
public class UsersController {

    private static final String MODIFY_USER_ERROR_MESSAGE = " Only a user can modified his info!";

    private final UserService userService;
    private final ModelMapper mapper;


    public UsersController(UserService userService, ModelMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }


    @Operation(summary = "List all users",
            description = "For users with admin rights only. Filtering possible",
            tags = {"admins"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully displayed all users.", content = @Content(schema = @Schema(implementation = UserOutDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Wrong inserted date filters.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("")
    public ResponseEntity<List<UserOutDTO>> getAll(
            @Parameter(description = "Partial or full first name")
            @RequestParam(required = false) String firstName,
            @Parameter(description = "Partial or full last name")
            @RequestParam(required = false) String lastName,
            @Parameter(description = "Partial or full email")
            @RequestParam(required = false) String email,
            @Parameter(description = "Partial or full username")
            @RequestParam(required = false) String username,
            @Parameter(description = "Pattern - [</>/<=/>=/<>/=],YYYY-MM-DD HH:mm:ss")
            @RequestParam(required = false) String registered,
            @Parameter(description = "Pattern - [</>/<=/>=/<>/=],YYYY-MM-DD HH:mm:ss")
            @RequestParam(required = false) String updated,
            @RequestParam(required = false) Boolean isBlocked,
            @RequestParam(required = false) Boolean isDeleted,
            @Parameter(description = "Pattern - roleName1,roleName2,rolename3")
            @RequestParam(required = false) String authority,
            @Parameter(description = "Options - all field names and (year/month/day)Registered/Updated")
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder
    ) {
        UserFilterOptions userFilterOptions =
                new UserFilterOptions(
                        firstName, lastName, email, username,
                        registered, updated, isBlocked, isDeleted,
                        authority, sortBy, sortOrder
                );

        List<User> users = userService.getAll(userFilterOptions);
        List<UserOutDTO> userOutDTOS = users.stream()
                .map(user -> mapper.map(user, UserOutDTO.class))
                .toList();

        return ResponseEntity.ok(userOutDTOS);
    }

    @Operation(summary = "Find user by ID.",
            tags = {"users"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully.", content = @Content(schema = @Schema(implementation = UserOutDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("/{userId}")
    public ResponseEntity<UserOutDTO> getById(@Parameter(description = "Wanted user id") @PathVariable long userId) {

        UserOutDTO dto = mapper.map(userService.get(userId), UserOutDTO.class);

        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Show personal data",
            description = "Retrieve full personal info.",
            tags = {"users"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully.", content = @Content(schema = @Schema(implementation = UserOutDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "No such user", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());

        return ResponseEntity.ok(currentUser);
    }

    @Operation(summary = "Find user's posts.",
            description = "Find all post by a specific user. Filter possible",
            tags = {"users"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully.", content = @Content(schema = @Schema(implementation = PostOutFullDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Wrong inserted date filters.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostOutFullDTO>> getUserPosts(@Parameter(description = "Target User ID", required = true)
                                                             @PathVariable long userId,
                                                             @Parameter(description = "Partial or full title")
                                                             @RequestParam(required = false) String title,
                                                             @Parameter(description = "Partial or full words")
                                                             @RequestParam(required = false) String content,
                                                             @Parameter(description = "Pattern - tagId1,tagId2,tagId3")
                                                             @RequestParam(required = false) String tags,
                                                             @Parameter(description = "Pattern - [</>/<=/>=/<>/=],YYYY-MM-DD HH:mm:ss")
                                                             @RequestParam(required = false) String created,
                                                             @Parameter(description = "Pattern - [</>/<=/>=/<>/=],YYYY-MM-DD HH:mm:ss")
                                                             @RequestParam(required = false) String updated,
                                                             @Parameter(description = "Options - all field names and (year/month/day)Created/Updated")
                                                             @RequestParam(required = false) String sortBy,
                                                             @RequestParam(required = false) String sortOrder
    ) {


        PostFilterOptions postFilterOptions =
                new PostFilterOptions(title, content, userId, tags, created, updated, sortBy, sortOrder);

        List<Post> filteredPosts = userService.getSpecificUserPosts(userId, postFilterOptions);

        List<PostOutFullDTO> postOutFullDTOS = filteredPosts.stream()
                .map(post -> mapper.map(post, PostOutFullDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(postOutFullDTOS);
    }

    @Operation(summary = "Find user's comments.",
            description = "Find all comments by a specific user. Filter possible",
            tags = {"users"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully.", content = @Content(schema = @Schema(implementation = CommentOutDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Wrong inserted date filters.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("/{userId}/comments")
    public ResponseEntity<List<CommentOutDTO>> getUserComments(@Parameter(description = "Target User ID", required = true)
                                                               @PathVariable long userId,
                                                               @Parameter(description = "Partial or full words")
                                                               @RequestParam(required = false) String content,
                                                               @Parameter(description = "Parent post id")
                                                               @RequestParam(required = false) Long commentedTo,
                                                               @Parameter(description = "Pattern - [</>/<=/>=/<>/=],YYYY-MM-DD HH:mm:ss")
                                                               @RequestParam(required = false) String created,
                                                               @Parameter(description = "Pattern - [</>/<=/>=/<>/=],YYYY-MM-DD HH:mm:ss")
                                                               @RequestParam(required = false) String updated,
                                                               @Parameter(description = "Options - all field names and (year/month/day)Created/Updated")
                                                               @RequestParam(required = false) String sortBy,
                                                               @RequestParam(required = false) String sortOrder) {

        CommentFilterOptions commentFilterOptions =
                new CommentFilterOptions(content, created, updated, userId, commentedTo, sortBy, sortOrder);

        List<Comment> filteredComments = userService.getSpecificUserComments(userId, commentFilterOptions);

        List<CommentOutDTO> commentOutDTOS = filteredComments.stream()
                .map(comment -> mapper.map(comment, CommentOutDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentOutDTOS);
    }

    @Operation(summary = "Update user's info",
            description = "Can change only personal info. Cannot change username",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User details for updating a logged user",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateDTO.class))
            ),
            tags = {"users"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated.", content = @Content(schema = @Schema(implementation = UserOutDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Modifying other user info", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "406", description = "Username cannot change", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Provided email is taken", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PutMapping("/{userId}")
    public ResponseEntity<UserOutDTO> update(@Parameter(description = "Target user ID", required = true)
                                             @PathVariable long userId,
                                             @Parameter(description = "Updated user info", required = true)
                                             @Valid @RequestBody UserUpdateDTO dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        User user = mapper.map(dto, User.class);

        User updatedUser = userService.update(userId, currentUser, user);

        return ResponseEntity.ok(mapper.map(updatedUser, UserOutDTO.class));
    }
}
