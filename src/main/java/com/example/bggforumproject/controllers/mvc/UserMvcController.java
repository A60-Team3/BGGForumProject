package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.request.UserUpdateDTO;
import com.example.bggforumproject.dtos.response.UserOutDTO;
import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.exceptions.IllegalFileUploadException;
import com.example.bggforumproject.models.*;
import com.example.bggforumproject.security.CustomUserDetails;
import com.example.bggforumproject.service.contacts.*;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.BeanUtils;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/BGGForum/users")
public class UserMvcController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final ReactionService reactionService;
    private final PictureService pictureService;
    private final PhoneService phoneService;
    private final ModelMapper mapper;

    public UserMvcController(UserService userService, PostService postService, CommentService commentService, ReactionService reactionService, PictureService pictureService, PhoneService phoneService, ModelMapper mapper) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.reactionService = reactionService;
        this.pictureService = pictureService;
        this.phoneService = phoneService;
        this.mapper = mapper;
    }

    @ModelAttribute("principalPhoto")
    public String principalPhoto(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        ProfilePicture profilePicture = pictureService.get(customUserDetails.getId());
        if (profilePicture != null) {
            return profilePicture.getPhotoUrl();
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

        Page<Post> specificUserPosts = userService.getSpecificUserPosts(userId, pageIndex, pageSize);
        Page<Comment> userComments = userService.getSpecificUserComments(userId, pageIndex, pageSize);
        List<Reaction> userReactions = reactionService.getAllByUser(userId);

        List<Long> userPosts = specificUserPosts.get().map(Post::getId).toList();
        ProfilePicture profilePicture = pictureService.get(user.getId());
        String userPhone = phoneService.get(user.getId());

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
        model.addAttribute("userPicture", profilePicture);
        model.addAttribute("userPhone", userPhone);
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
        ProfilePicture profilePicture = pictureService.get(user.getId());
        String userPhone = phoneService.get(user.getId());

        if (user.getId() != userId) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", "Only Owner can update his info");
            return new ModelAndView("error-page");
        }

        model.addAttribute("user", user);
        model.addAttribute("userPicture", profilePicture);
        model.addAttribute("userPhone", userPhone);

        return new ModelAndView("user-update");
    }

    @PostMapping("/{userId}/update")
    public String update(@PathVariable long userId,
                         @ModelAttribute("userUpdateDto") UserUpdateDTO dto,
                         BindingResult bindingResult,
                         Model model,
                         @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            return "user-update";
        }

        if (!dto.password().equals(dto.passwordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
            return "user-update";
        }

        User loggedUser = userService.get(userDetails.getUsername());

        if (userId != loggedUser.getId()) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", "You can edit only your own info");
            return "error-page";
        }

        User user = mapper.map(dto, User.class);

        userService.update(userId, loggedUser, user);

        if (dto.phoneNumber() != null) {
            phoneService.savePhone(dto.phoneNumber(),loggedUser);
        }

        model.addAttribute("successMessage","User details successfully updated");

        return "redirect:/BGGForum/users/" + user.getId() + "?success";
    }

    @PostMapping("/{userId}/update/upload")
    public String changeProfilePicture(@PathVariable long userId,
                                       @ModelAttribute("userUpdateDto") UserUpdateDTO dto,
                                       Model model,
                                       @AuthenticationPrincipal UserDetails loggedUser) throws IOException {
        User user = userService.get(loggedUser.getUsername());

        try {
            userService.uploadProfilePic(dto.profilePic(), user, userId);
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        } catch (IllegalFileUploadException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        model.addAttribute("successMessage","Profile picture updated");

        return "redirect:/BGGForum/users/" + user.getId();
    }
}
