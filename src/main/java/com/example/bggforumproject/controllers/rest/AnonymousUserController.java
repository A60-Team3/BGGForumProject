package com.example.bggforumproject.controllers.rest;

import com.example.bggforumproject.dtos.response.ApiErrorResponseDTO;
import com.example.bggforumproject.dtos.response.PostAnonymousOutDTO;
import com.example.bggforumproject.dtos.request.UnknownOutDTO;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.service.contacts.AnonymousUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

//@RestController
//@RequestMapping("/BGGForum")
@Tag(name = "free", description = "The Free Access API")
public class AnonymousUserController {
    private final AnonymousUserService anonymousUserService;
    private final ModelMapper mapper;

    public AnonymousUserController(AnonymousUserService anonymousUserService, ModelMapper mapper) {
        this.anonymousUserService = anonymousUserService;
        this.mapper = mapper;
    }

    @Operation(summary = "Main page.",
            description = "Returns count users, count posts and all posts info. Can be filtered",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully received forums info with applied filters.",
                            content = @Content(schema = @Schema(implementation = UnknownOutDTO.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Wrong inserted date filters.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @GetMapping("/main")
    public UnknownOutDTO mainPage(@Parameter(description = "Page number, default is 0.")
                                  @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                  @Parameter(description = "Page size, default is 10.")
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                  @Parameter(description = "Partly or full title")
                                  @RequestParam(required = false) String title,
                                  @Parameter(description = "Pattern - [</>/<=/>=/<>/=]")
                                      @RequestParam(required = false) String createdCondition,
                                  @Parameter(description = "Pattern - YYYY-MM-DD HH:mm:ss")
                                  @RequestParam(required = false) LocalDateTime created,
                                  @Parameter(description = "Pattern - tagId1,tagId2,tagId3")
                                  @RequestParam(required = false) List<Long> tags,
                                  @Parameter(description = "Options - title/created")
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder) {

        PostFilterOptions postFilterOptions =
                new PostFilterOptions(
                        title, null, null, tags,
                        createdCondition, created, null, null,
                        sortBy, sortOrder
                );
        long users = anonymousUserService.countUsers();
        long posts = anonymousUserService.countPosts();

        Page<Post> filteredPosts = anonymousUserService.getAllPosts(postFilterOptions, pageIndex, pageSize);

        List<PostAnonymousOutDTO> postAnonymousOutDTOS = filteredPosts.stream()
                .map(post -> mapper.map(post, PostAnonymousOutDTO.class))
                .toList();

        return new UnknownOutDTO(users, posts, postAnonymousOutDTOS);
    }

    @Operation(summary = "Most commented posts.",
            description = "Return top 10 commented posts in descending order",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully displayed top 10 commented posts.",
                            content = @Content(schema = @Schema(implementation = PostAnonymousOutDTO.class)))
            })
    @GetMapping("/posts/most-commented")
    public ResponseEntity<List<PostAnonymousOutDTO>> getMostCommented() {
        List<Post> mostCommented = anonymousUserService.getMostCommented();

        List<PostAnonymousOutDTO> postAnonymousOutDTOS = mostCommented.stream()
                .map(post -> mapper.map(post, PostAnonymousOutDTO.class))
                .toList();

        return ResponseEntity.ok(postAnonymousOutDTOS);
    }

    @Operation(summary = "Most recent posts.",
            description = "Return 10 most recent posts in descending order",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully displayed 10 most recent posts.",
                            content = @Content(schema = @Schema(implementation = PostAnonymousOutDTO.class)))
            })
    @GetMapping("/posts/most-recently-created")
    public ResponseEntity<List<PostAnonymousOutDTO>> getMostRecentlyCreated(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                                                            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Page<Post> mostRecentlyCreated = anonymousUserService.getMostRecentlyCreated(pageIndex, pageSize);

        List<PostAnonymousOutDTO> postAnonymousOutDTOS = mostRecentlyCreated.stream()
                .map(post -> mapper.map(post, PostAnonymousOutDTO.class))
                .toList();

        return ResponseEntity.ok(postAnonymousOutDTOS);
    }
}
