package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.CommentService;
import com.example.bggforumproject.service.contacts.PostService;
import com.example.bggforumproject.service.contacts.ReactionService;
import com.example.bggforumproject.service.contacts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/{userId}")
    public String showSingleUserPage(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                     @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                     @PathVariable long userId, Model model) {
        User user = userService.get(userId);

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


        model.addAttribute("shownUser",user);
        model.addAttribute("userPosts",userPosts);
        model.addAttribute("commentedPosts",commentedPosts);
        model.addAttribute("reactedPosts",reactedPosts);
        return "user-single";
    }

    @GetMapping("/profile")
    public String showProfilePage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.get(username);
        return "redirect:/BGGForum/users/" + user.getId();
    }
}
