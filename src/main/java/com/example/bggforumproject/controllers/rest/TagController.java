package com.example.bggforumproject.controllers.rest;

import com.example.bggforumproject.dtos.ApiErrorResponseDTO;
import com.example.bggforumproject.dtos.PostOutFullDTO;
import com.example.bggforumproject.dtos.TagOutDTO;
import com.example.bggforumproject.helpers.filters.TagFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.TagService;
import com.example.bggforumproject.service.contacts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/BGGForum/posts")
public class TagController {

    private final TagService tagService;
    private final UserService userService;
    private final ModelMapper mapper;

    public TagController(TagService tagService, UserService userService, ModelMapper mapper) {
        this.tagService = tagService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Operation(summary = "List all tags.",
            description = "Admin rights only. Filtration available",
            tags = {"admins"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully list requested tags.", content = @Content(schema = @Schema(implementation = TagOutDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Admins only", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("/tags")
    public ResponseEntity<List<TagOutDTO>> getAll(@Parameter(description = "Find specific tag by id")
                                                  @RequestParam(required = false) Long tagId,
                                                  @Parameter(description = "Partly or full name")
                                                  @RequestParam(required = false) String name,
                                                  @Parameter(description = "Pattern - postId1,postId2,postId3")
                                                  @RequestParam(required = false) String postIds,
                                                  @Parameter(description = "Options - name/postId")
                                                  @RequestParam(required = false) String sortBy,
                                                  @RequestParam(required = false) String sortOrder) {

        TagFilterOptions tagFilterOptions = new TagFilterOptions(tagId, name, postIds, sortBy, sortOrder);

        List<Tag> tags = tagService.get(tagFilterOptions);

        List<TagOutDTO> tagOutDTOS = tags.stream().map(tag -> mapper.map(tag, TagOutDTO.class)).toList();

        return ResponseEntity.ok(tagOutDTOS);
    }

    @Operation(summary = "Add a tag to specific post.",
            description = "Creates the tag, if it doesnt exist in the system. Only post author can add.",
            tags = {"tags"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully assigned tag to post.", content = @Content(schema = @Schema(implementation = PostOutFullDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Post creator only", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Tag already assigned to the post.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PostMapping("/{postId}/tags")
    public ResponseEntity<PostOutFullDTO> addTagToPost(@Parameter(description = "Target post id", required = true)
                                                       @PathVariable long postId,
                                                       @Parameter(description = "Tag name", required = true)
                                                       @NotBlank @RequestParam String tagName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        Post post = tagService.addTagToPost(postId, tagName, currentUser);

        PostOutFullDTO postOutFullDTO = mapper.map(post, PostOutFullDTO.class);

        return new ResponseEntity<>(postOutFullDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Removes a tag from specific post.",
            description = "Removes the tag. Only post author can remove.",
            tags = {"tags"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted tag from post."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Post creator only", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post/Tag with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "No such tag assigned to the post.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @DeleteMapping("/{postId}/tags/{tagId}")
    public ResponseEntity<?> deleteTagFromPost(@Parameter(description = "Target post id", required = true)
                                               @PathVariable long postId,
                                               @Parameter(description = "Tag to remove ID", required = true)
                                               @PathVariable long tagId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());

        tagService.deleteTagFromPost(tagId, postId, currentUser);

        return ResponseEntity.ok("Tag removed from the post successfully");
    }

    @Operation(summary = "Removes a tag from the database.",
            description = "Removes the tag complete. Only users with admin rights.",
            tags = {"admins"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted tag from post."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden. Admin rights only", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Post/Tag with such id doesnt exist.", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable long tagId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        tagService.delete(tagId, currentUser);

        return ResponseEntity.ok("Tag removed from the system successfully");
    }
}
