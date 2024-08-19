package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.request.UserUpdateDTO;
import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.exceptions.IllegalFileUploadException;
import com.example.bggforumproject.models.*;
import com.example.bggforumproject.security.CustomUserDetails;
import com.example.bggforumproject.service.contacts.*;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
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
        ProfilePicture profilePicture =
                user.getProfilePicture() != null
                        ? pictureService.get(user.getProfilePicture().getPhotoUrl())
                        : null;
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
    public String showUserUpdatePage(
                                           @PathVariable long userId, Model model,
                                            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal.getId() != userId) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", "Only Owner can update his info");
            return "error-page";
        }

        User user = userService.get(principal.getUsername());

        ProfilePicture profilePicture =
                user.getProfilePicture() != null
                        ? pictureService.get(user.getProfilePicture().getPhotoUrl())
                        : null;

        String userPhone = phoneService.get(user.getId());
        UserUpdateDTO dto = new UserUpdateDTO(user.getFirstName(), user.getLastName(),
                user.getUsername(),user.getPassword(),null, user.getEmail(),
                userPhone,null);

        model.addAttribute("user", user);
        model.addAttribute("userPicture", profilePicture);
        model.addAttribute("userPhone", userPhone);
        model.addAttribute("userUpdateDto", dto);

        return "user-update";
    }

    @PreAuthorize("#userId == principal.getId()")
    @PostMapping("/{userId}/update")
    public String update(@PathVariable long userId,
                         @ModelAttribute("userUpdateDto") UserUpdateDTO dto,
                         BindingResult bindingResult,
                         Model model,
                         @AuthenticationPrincipal CustomUserDetails principal) {
        User loggedUser = userService.get(principal.getUsername());
        model.addAttribute("user", loggedUser);

        if (userId != principal.getId()) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", "You can edit only your own info");
            return "error-page";
        }

        if (bindingResult.hasErrors()) {
            return "user-update";
        }

        if (!dto.password().equals(dto.passwordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
            return "user-update";
        }


        User user = mapper.map(dto, User.class);
        user.setUsername(loggedUser.getUsername());

        userService.update(userId, loggedUser, user);

        if (dto.phoneNumber() != null) {
            phoneService.savePhone(dto.phoneNumber(), loggedUser);
        }

        model.addAttribute("successMessage", "User details successfully updated");

        return "redirect:/BGGForum/users/" + principal.getId() + "?success";
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

        model.addAttribute("successMessage", "Profile picture updated");

        return "redirect:/BGGForum/users/" + user.getId();
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable long userId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.get(username);

        if (userId != currentUser.getId()) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", "You can only delete yourself");
            return "error-page";
        }

        userService.delete(userId, currentUser);

        return "redirect:/BGGForum/logout";
    }
}
