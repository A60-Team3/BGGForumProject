package com.example.bggforumproject.controllers;

import com.example.bggforumproject.dtos.TagDTO;
import com.example.bggforumproject.dtos.TagsOutDTO;
import com.example.bggforumproject.helpers.filters.TagFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.PostService;
import com.example.bggforumproject.service.contacts.TagService;
import com.example.bggforumproject.service.contacts.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BGGForum/posts")
public class TagController {

    private final TagService tagService;
    private final PostService postService;
    private final UserService userService;
    private final ModelMapper mapper;

    public TagController(TagService tagService, PostService postService, UserService userService, ModelMapper mapper) {
        this.tagService = tagService;
        this.postService = postService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagsOutDTO>> getAll(@RequestParam(required = false) Long tagId,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String postIds,
                                   @RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false) String sortOrder) {

        TagFilterOptions tagFilterOptions = new TagFilterOptions(tagId, name, postIds, sortBy, sortOrder);

        List<Tag> tags = tagService.get(tagFilterOptions);

        List<TagsOutDTO> tagsOutDTOS =  tags.stream().map(tag -> mapper.map(tag, TagsOutDTO.class)).toList();

        return ResponseEntity.ok(tagsOutDTOS);
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

    @DeleteMapping("/{postId}/tags/{tagId}")
    public ResponseEntity<?> deleteTagFromPost(@PathVariable long postId, @PathVariable long tagId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());

        tagService.deleteTagFromPost(tagId, tagId, currentUser);

        return ResponseEntity.ok("Tag removed from post successfully");
    }

    @DeleteMapping("/tags/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.get(authentication.getName());
        tagService.delete(id, currentUser);

        return ResponseEntity.ok("Tag removed from the system successfully");
    }
}
