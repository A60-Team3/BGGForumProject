package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.FilterDto;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.helpers.filters.TagFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.service.contacts.AnonymousUserService;
import com.example.bggforumproject.service.contacts.TagService;
import org.springframework.data.domain.Page;
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

    public AnonymousMvcController(TagService tagService, AnonymousUserService anonymousUserService) {
        this.tagService = tagService;
        this.anonymousUserService = anonymousUserService;
    }

    @ModelAttribute("tags")
    public List<Tag> populateTags() {
        return tagService.get(new TagFilterOptions(null, null, null, null, null));
    }

    @GetMapping("/main")
    public String getHomePage(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                              @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                              @ModelAttribute("postFilterOptions") FilterDto dto, Model model) {
        PostFilterOptions postFilterOptions = new PostFilterOptions(
                (dto.title() != null && dto.title().isEmpty()) ? null : dto.title(),
                (dto.content() != null && dto.content().isEmpty()) ? null : dto.content(),
                dto.userId(),
                (dto.tags() != null && dto.tags().isEmpty()) ? null : dto.tags(),
                (dto.createCondition() != null && dto.createCondition().isEmpty()) ? null : dto.createCondition(),
                dto.created(),
                (dto.updateCondition() != null && dto.updateCondition().isEmpty()) ? null : dto.updateCondition(),
                dto.updated(),
                (dto.sortBy() != null && dto.sortBy().isEmpty()) ? null : dto.sortBy(),
                (dto.sortOrder() != null && dto.sortOrder().isEmpty()) ? null : dto.sortOrder()
        );

        Page<Post> posts = anonymousUserService.getAllPosts(postFilterOptions, pageIndex, pageSize);

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("pagePosts", posts);
        model.addAttribute("currentPage", posts.getNumber() + 1);
        model.addAttribute("totalItems", posts.getTotalElements());
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalUsers", anonymousUserService.countUsers());
        model.addAttribute("totalPosts", anonymousUserService.countPosts());
        return "main";
    }

}
