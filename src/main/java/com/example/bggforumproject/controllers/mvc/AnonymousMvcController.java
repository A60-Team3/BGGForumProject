package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.request.FilterDto;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.helpers.filters.TagFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.security.CustomUserDetails;
import com.example.bggforumproject.service.contacts.AnonymousUserService;
import com.example.bggforumproject.service.contacts.PictureService;
import com.example.bggforumproject.service.contacts.TagService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/BGGForum")
public class AnonymousMvcController {
    private final TagService tagService;
    private final AnonymousUserService anonymousUserService;
    private final PictureService pictureService;

    public AnonymousMvcController(TagService tagService, AnonymousUserService anonymousUserService, PictureService pictureService) {
        this.tagService = tagService;
        this.anonymousUserService = anonymousUserService;
        this.pictureService = pictureService;
    }

    @ModelAttribute("principalPhoto")
    public String principalPhoto(@AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return null;
        }

        ProfilePicture profilePicture = anonymousUserService.get(principal.getUsername()).getProfilePicture();
        if (profilePicture != null) {
            return profilePicture.getPhotoUrl();
        }
        return "/images/blank_profile.png";
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("tags")
    public List<Tag> populateTags() {
        return tagService.get(new TagFilterOptions(null, null, null, null, null));
    }

    @GetMapping("/main")
    public String getHomePage(@RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                              @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                              @ModelAttribute("postFilterOptions") FilterDto dto,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        PostFilterOptions postFilterOptions = new PostFilterOptions(
                (dto.title() != null && dto.title().isEmpty()) ? null : dto.title(),
                (dto.content() != null && dto.content().isEmpty()) ? null : dto.content(),
                dto.userId(),
                (dto.tags() != null && dto.tags().isEmpty()) ? null : dto.tags(),
                (dto.postIds() != null && dto.postIds().isEmpty()) ? null : dto.postIds(),
                (dto.createCondition() != null && dto.createCondition().isEmpty()) ? null : dto.createCondition(),
                dto.created(),
                (dto.updateCondition() != null && dto.updateCondition().isEmpty()) ? null : dto.updateCondition(),
                dto.updated(),
                (dto.sortBy() != null && dto.sortBy().isEmpty()) ? null : dto.sortBy(),
                (dto.sortOrder() != null && dto.sortOrder().isEmpty()) ? null : dto.sortOrder()
        );

        Page<Post> posts = anonymousUserService.getAllPosts(postFilterOptions, pageIndex - 1, pageSize);
        List<Post> mostCommented = anonymousUserService.getMostCommented();

        model.addAttribute("postsCommented", mostCommented);
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("currentPage", posts.getNumber() + 1);
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("queryCount", posts.getTotalElements());
        model.addAttribute("totalUsers", anonymousUserService.countUsers());
        model.addAttribute("totalPosts", anonymousUserService.countPosts());
        return "main";
    }

}
