package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.response.CommentDTO;
import com.example.bggforumproject.dtos.request.FilterDto;
import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.security.CustomUserDetails;
import com.example.bggforumproject.service.contacts.CommentService;
import com.example.bggforumproject.service.contacts.PictureService;
import com.example.bggforumproject.service.contacts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/BGGForum/posts")
public class CommentMvcController {

    private final CommentService commentService;
    private final UserService userService;
    private final ModelMapper mapper;

    public CommentMvcController(CommentService commentService,
                                UserService userService,
                                ModelMapper mapper) {
        this.commentService = commentService;
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

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/{postId}/comments")
    public String getComments(@PathVariable long postId,
                              @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                              @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                              @ModelAttribute("commentFilterOptions") FilterDto dto, Model model) {

        CommentFilterOptions commentFilterOptions = new CommentFilterOptions(
                dto.content(),
                dto.createCondition(),
                dto.updateCondition(),
                dto.userId(),
                postId,
                dto.sortBy(),
                dto.sortOrder()
        );

        Page<Comment> comments = commentService.getCommentsForPost(postId, pageIndex, pageSize);

        model.addAttribute("comments", comments.getContent());
        model.addAttribute("pageComments", comments);
        model.addAttribute("currentPage", comments.getNumber() + 1);
        model.addAttribute("totalPages", comments.getTotalPages());
        model.addAttribute("pageSize", pageSize);

        return "comments";
    }

    //TODO consume the postId variable somehow
    @GetMapping("/{postId}/comments/new")
    public String showNewCommentPage(@PathVariable long postId, Model model) {

        model.addAttribute("comment", new CommentDTO());
        return "create-comment";
    }

    @PostMapping("/{postId}/comments/new")
    public String createComment(@PathVariable long postId,
                                @Valid @ModelAttribute("comment") CommentDTO dto,
                                BindingResult bindingResult) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(authentication.getName());

        if (bindingResult.hasErrors()) {
            return "redirect:/BGGForum/posts/{postId}#comment_form";
        }

        Comment comment = mapper.map(dto, Comment.class);
        commentService.create(postId, comment, user);
        return "redirect:/BGGForum/posts/{postId}#comment_section";
    }

    @GetMapping("/{postId}/comments/{commentId}/update")
    public String showUpdateCommentPage(@PathVariable long postId,
                                        @PathVariable long commentId,
                                        Model model) {
        try {
            Comment comment = commentService.get(commentId);
            CommentDTO commentDTO = mapper.map(comment, CommentDTO.class);
            model.addAttribute("postId", postId);
            model.addAttribute("commentId", commentId);
            model.addAttribute("comment", commentDTO);
            return "comment-edit";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

    }

    @PostMapping("/{postId}/comments/{commentId}/update")
    public String updateComment(@PathVariable long postId,
                                @PathVariable long commentId,
                                @Valid @ModelAttribute("comment") CommentDTO dto,
                                BindingResult bindingResult,
                                Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(authentication.getName());
        if (bindingResult.hasErrors()) {
            return "comment-edit";
        }

        try {
            Comment comment = mapper.map(dto, Comment.class);
            commentService.update(commentId, postId, comment.getContent(), user);
            return "redirect:/BGGForum/posts/{postId}#comment_section";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("content", "duplicate_comment", e.getMessage());
            return "comment-edit";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

    }

    @GetMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable long postId,
                                @PathVariable long commentId,
                                Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(authentication.getName());

        try {
            commentService.delete(commentId, postId, user);
            return "redirect:/BGGForum/posts/{postId}#comment_section";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }
    }
}
