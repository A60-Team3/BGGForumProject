package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.response.TagDTO;
import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.helpers.filters.TagFilterOptions;
import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.security.CustomUserDetails;
import com.example.bggforumproject.service.contacts.PictureService;
import com.example.bggforumproject.service.contacts.TagService;
import com.example.bggforumproject.service.contacts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/BGGForum/posts")
public class TagMvcController {

    private final TagService tagService;
    private final UserService userService;
    private final ModelMapper mapper;

    public TagMvcController(TagService tagService, UserService userService, ModelMapper mapper) {
        this.tagService = tagService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @ModelAttribute("principalPhoto")
    public String principalPhoto(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ProfilePicture profilePicture = userService.get(customUserDetails.getUsername()).getProfilePicture();
        if (profilePicture != null) {
            return profilePicture.getPhotoUrl();
        }
        return "/images/blank_profile.png";
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("tags")
    public List<Tag> populateTags() {
        return tagService.get(new TagFilterOptions(null, null, null, null, null));
    }

    @GetMapping("/{postId}/tags")
    public String showNewTagPage(@PathVariable long postId, Model model, HttpSession session){
        if(!populateIsAuthenticated(session)){
            return "redirect:/auth/login";
        }

        model.addAttribute("tag", new TagDTO());
        return "add-tag";
    }


    @PostMapping("/{postId}/tags")
    public String addTagToPost(@PathVariable long postId,
                               @Valid @ModelAttribute("tag") TagDTO dto,
                               BindingResult bindingResult,
                               Model model,
                               @AuthenticationPrincipal UserDetails loggedUser){

        User user = userService.get(loggedUser.getUsername());

        if (bindingResult.hasErrors()){
            return "redirect:/BGGForum/posts/{postId}/update#tag-input";
        }
        try {
            Tag tag = mapper.map(dto, Tag.class);
            tagService.addTagToPost(postId, tag.getName(), user);
        } catch (EntityDuplicateException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:/BGGForum/posts/{postId}/update#tag-input";
    }

    @GetMapping("/{postId}/tags/{tagId}/remove")
    public String deleteTagFromPost(@PathVariable long postId,
                                    @PathVariable long tagId, Model model,
                                    @AuthenticationPrincipal UserDetails loggedUser){
        User user = userService.get(loggedUser.getUsername());
        try {
            tagService.deleteTagFromPost(tagId, postId, user);
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:/BGGForum/posts/{postId}";

    }

    @GetMapping("/tags/{tagId}")
    public String deleteTag(@PathVariable long tagId, HttpSession session){
        if(!populateIsAuthenticated(session)){
            return "redirect:/auth/login";
        }

        User user = userService.get((String) session.getAttribute("currentUser"));
        tagService.delete(tagId, user);
        return "redirect:/BGGForum/posts/tags";
    }
}
