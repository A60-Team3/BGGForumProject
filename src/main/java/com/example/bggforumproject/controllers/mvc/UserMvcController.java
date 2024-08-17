package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.request.UserUpdateDTO;
import com.example.bggforumproject.dtos.response.UserOutDTO;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.security.CustomUserDetails;
import com.example.bggforumproject.service.contacts.CommentService;
import com.example.bggforumproject.service.contacts.PostService;
import com.example.bggforumproject.service.contacts.ReactionService;
import com.example.bggforumproject.service.contacts.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/BGGForum/users")
public class UserMvcController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final ReactionService reactionService;

    public UserMvcController(UserService userService, PostService postService, CommentService commentService, ReactionService reactionService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.reactionService = reactionService;
    }

    @ModelAttribute("principalPhoto")
    public String principalPhoto(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        if (customUserDetails.getPhotoUrl() != null) {
            return customUserDetails.getPhotoUrl();
        }
        return "/images/blank_profile.png";
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/{userId}")
    public String showSingleUserPage(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                     @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                     @PathVariable long userId, Model model) {

        User user;
        try {
            user = userService.get(userId);
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        Page<Post> fromDB = userService.getSpecificUserPosts(userId, pageIndex, pageSize);
        Page<Comment> userComments = userService.getSpecificUserComments(userId, pageIndex, pageSize);
        List<Reaction> userReactions = reactionService.getAllByUser(userId);

        List<Long> userPosts = fromDB.get().map(Post::getId).toList();

        List<Long> reactedPosts = userReactions.stream()
                .map(Reaction::getPostId)
                .map(Post::getId)
                .distinct()
                .toList();

        List<Long> commentedPosts = userComments.get()
                .map(Comment::getPostId)
                .map(Post::getId)
                .distinct()
                .toList();


        model.addAttribute("shownUser", user);
        model.addAttribute("userPosts", userPosts);
        model.addAttribute("commentedPosts", commentedPosts);
        model.addAttribute("reactedPosts", reactedPosts);
        return "user-single";
    }

    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return "redirect:/BGGForum/users/" + userDetails.getId();
    }

    @PreAuthorize("#userId == principal.getId()")
    @GetMapping("/{userId}/update")
    public ModelAndView showUserUpdatePage(@ModelAttribute("userUpdateDto") UserUpdateDTO dto,
                                           @PathVariable long userId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.get(username);

        if (user.getId() != userId) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", "Only Owner can update his info");
            return new ModelAndView("error-page");
        }

        model.addAttribute("user", user);

        return new ModelAndView("user-update");
    }

    @PostMapping("/{userId}/update")
    public ResponseEntity<UserOutDTO> update(@PathVariable long userId,
                                             @ModelAttribute("userUpdateDto") UserUpdateDTO dto) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = userService.get(authentication.getName());
//
//        User user = mapper.map(dto, User.class);
//
//        User updatedUser = userService.update(userId, currentUser, user);

        return null;
    }

    @PostMapping("/{userId}")
    public String changeProfilePicture() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.get(username);
        return "redirect:/BGGForum/users/" + user.getId();
    }
}
