package com.example.bggforumproject.controllers.rest;

import com.example.bggforumproject.dtos.response.ApiErrorResponseDTO;
import com.example.bggforumproject.dtos.response.UserBlockOutDTO;
import com.example.bggforumproject.dtos.response.UserOutDTO;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

////@RestController
//@RequestMapping("/BGGForum/admin")
@Tag(name = "admins", description = "The Admin Privileges Only API")
public class AdminController {
    private final UserService userService;
    private final ModelMapper mapper;

    public AdminController(UserService userService, ModelMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Operation(summary = "Change user's block status.",
            description = "Block or unblock user.",
            tags = {"admins"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully changed user's block status.", content = @Content(schema = @Schema(implementation = UserBlockOutDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Admin rights missing", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "The user block status is the same.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PutMapping("/{userId}")
    public ResponseEntity<UserBlockOutDTO> blockUser(
            @Parameter(description = "Target user id", required = true)
            @PathVariable long userId,
            @Parameter(description = "Updated block status", required = true)
            @NotBlank @RequestParam boolean isBlocked) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        User blocked = userService.blockUser(userId, currentUser);

        return ResponseEntity.ok(mapper.map(blocked, UserBlockOutDTO.class));
    }

    @Operation(summary = "User promotions. Admin only.",
            description = "Promote user to moderator.",
            tags = {"admins"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully promoted user to moderator.", content = @Content(schema = @Schema(implementation = UserOutDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Admins only", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "The user is already a moderator.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PutMapping("/admin/{userId}")
    public ResponseEntity<UserOutDTO> promoteUser(@Parameter(description = "Promoted user id", required = true)
                                                  @PathVariable long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        User user = userService.promote(userId, currentUser);
        UserOutDTO dto = mapper.map(user, UserOutDTO.class);

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "User soft delete. Admin only.",
            description = "User is marked for deletion. Deactivates user. Not reversible",
            tags = {"admins"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully marked user to deletion."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Admins only", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "The user is already a moderator.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PutMapping("/admin/{userId}/archive")
    public void archiveUser(@Parameter(description = "Deleted user id", required = true)
                            @PathVariable long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        userService.softDelete(userId, currentUser);
    }

    @Operation(summary = "User termination. Admin only.",
            description = "Complete removal of user from database. Not reversible",
            tags = {"admins"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully terminated user."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Admins only", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            })
    @DeleteMapping("/admin/{userId}")
    public void deleteUser(@Parameter(description = "Terminated user id", required = true)
                           @PathVariable long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        userService.delete(userId, currentUser);
    }
}
