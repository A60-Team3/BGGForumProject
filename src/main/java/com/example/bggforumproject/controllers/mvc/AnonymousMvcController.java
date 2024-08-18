package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.request.FilterDto;
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
    public String principalPhoto(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        if (customUserDetails == null){
            return null;
        }

        ProfilePicture profilePicture = anonymousUserService.get(customUserDetails.getUsername()).getProfilePicture();
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
    public String getHomePage(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                              @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                              @ModelAttribute("postFilterOptions") FilterDto dto,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        List<Post> mostCommented = anonymousUserService.getMostCommented();
        Page<Post> mostRecentlyCreated = anonymousUserService.getMostRecentlyCreated(pageIndex, pageSize);

        model.addAttribute("postsCommented", mostCommented);
        model.addAttribute("postsRecent", mostRecentlyCreated.getContent());
        model.addAttribute("currentPage", mostRecentlyCreated.getNumber() + 1);
        model.addAttribute("totalPages", mostRecentlyCreated.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalUsers", anonymousUserService.countUsers());
        model.addAttribute("totalPosts", anonymousUserService.countPosts());
        return "main";
    }

}
