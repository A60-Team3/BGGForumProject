package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.response.ReactionDTO;
import com.example.bggforumproject.helpers.StringToReactionTypeConverter;
import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.ReactionType;
import com.example.bggforumproject.security.CustomUserDetails;
import com.example.bggforumproject.service.contacts.PictureService;
import com.example.bggforumproject.service.contacts.ReactionService;
import com.example.bggforumproject.service.contacts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/BGGForum/posts")
public class ReactionMvcController {

    private final ReactionService reactionService;
    private final UserService userService;
    private final ModelMapper mapper;
    private final StringToReactionTypeConverter converter;

    public ReactionMvcController(ReactionService reactionService, UserService userService, ModelMapper mapper, StringToReactionTypeConverter converter) {
        this.reactionService = reactionService;
        this.userService = userService;
        this.mapper = mapper;
        this.converter = converter;
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

    @GetMapping("/{postId}/reactions")
    public String getReactions(@PathVariable long postId){
        return null;
    }

    @PostMapping("/{postId}/reactions/new")
    public String createReaction(@PathVariable long postId,
                                 @Valid @ModelAttribute("reaction") ReactionDTO reactionDto,
                                 @AuthenticationPrincipal UserDetails loggedUser){

        ReactionType reactionType = converter.convert(reactionDto.getReactionType());

        User user = userService.get(loggedUser.getUsername());
        Reaction reaction = new Reaction();
        reaction.setReactionType(reactionType);

        reactionService.create(reaction, user, postId);

        return "redirect:/BGGForum/posts/{postId}";
    }

    @PostMapping("/{postId}/reactions/{reactionId}/update")
    public String updateReaction(@PathVariable long postId,
                                 @PathVariable long reactionId,
                                 @ModelAttribute("reaction") ReactionDTO reactionDto,
                                 @AuthenticationPrincipal UserDetails loggedUser){
        User user = userService.get(loggedUser.getUsername());
        ReactionType reactionType = converter.convert(reactionDto.getReactionType());
        reactionService.update(reactionId, user, postId, reactionType);

        return "redirect:/BGGForum/posts/{postId}";
    }

    @GetMapping("/{postId}/reactions/{reactionId}/delete")
    public String deleteReaction(@PathVariable long postId,
                                 @PathVariable long reactionId,
                                 @AuthenticationPrincipal UserDetails loggedUser){

        User user = userService.get(loggedUser.getUsername());

        reactionService.delete(reactionId, user, postId);
        return "redirect:/BGGForum/posts/{postId}";
    }
}
