package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.request.FilterDto;
import com.example.bggforumproject.helpers.filters.UserFilterOptions;
import com.example.bggforumproject.models.PhoneNumber;
import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.security.CustomUserDetails;
import com.example.bggforumproject.service.contacts.PhoneService;
import com.example.bggforumproject.service.contacts.RoleService;
import com.example.bggforumproject.service.contacts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/BGGForum/admin")
public class AdminMvcController {

    private final UserService userService;
    private final RoleService roleService;
    private final PhoneService phoneService;

    public AdminMvcController(UserService userService, RoleService roleService, PhoneService phoneService){
        this.userService = userService;
        this.roleService = roleService;
        this.phoneService = phoneService;
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

    @ModelAttribute("roles")
    public List<String> collectRoleTypes() {
        return roleService.getAll().stream().map(Role::getAuthority).toList();
    }
    @ModelAttribute("phones")
    public List<PhoneNumber> collectPhones(){
        return phoneService.getAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping
    public String getAdminPage(@RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                               @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                               @ModelAttribute("userFilterOptions") FilterDto dto, Model model
    ) {
        UserFilterOptions userFilterOptions = new UserFilterOptions(
                (dto.firstName() != null && dto.firstName().isEmpty()) ? null : dto.firstName(),
                (dto.lastName() != null && dto.lastName().isEmpty()) ? null : dto.lastName(),
                (dto.email() != null && dto.email().isEmpty()) ? null : dto.email(),
                (dto.username() != null && dto.username().isEmpty()) ? null : dto.username(),
                (dto.createCondition() != null && dto.createCondition().isEmpty()) ? null : dto.createCondition(),
                dto.created(),
                (dto.updateCondition() != null && dto.updateCondition().isEmpty()) ? null : dto.updateCondition(),
                dto.updated(), dto.isBlocked(), dto.isDeleted(),
                (dto.roles() != null && dto.roles().isEmpty()) ? null : dto.roles(),
                (dto.phoneNumber() != null && dto.phoneNumber().isEmpty()) ? null : dto.phoneNumber(),
                (dto.sortBy() != null && dto.sortBy().isEmpty()) ? null : dto.sortBy(),
                (dto.sortOrder() != null && dto.sortOrder().isEmpty()) ? null : dto.sortOrder()
        );

        Page<User> all = userService.getAll(userFilterOptions, pageIndex - 1, pageSize);

        List<User> admins = userService.getAllAdmins();
        List<User> mods = userService.getAllModerators();
        List<User> allUsers = userService.getAll();

        model.addAttribute("users", all.getContent());
        model.addAttribute("mods", mods);
        model.addAttribute("admins", admins);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("currentPage", all.getNumber() + 1);
        model.addAttribute("totalItems", all.getTotalElements());
        model.addAttribute("totalPages", all.getTotalPages());
        model.addAttribute("pageSize", pageSize);

        return "admin";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/promote")
    public String promoteUser(@RequestParam(value = "pageIndex") int pageIndex,
                              @RequestParam(value = "pageSize") int pageSize,
                              @PathVariable long userId,
                              RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        redirectAttributes.addAttribute("pageIndex", pageIndex + 1);
        redirectAttributes.addAttribute("pageSize", pageSize);
        redirectAttributes.addAttribute("openModal", userId);

        currentUser = userService.promote(userId, currentUser);

        return "redirect:/BGGForum/admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/demote")
    public String demoteUser(@RequestParam(value = "pageIndex") int pageIndex,
                              @RequestParam(value = "pageSize") int pageSize,
                              @PathVariable long userId,
                              RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        redirectAttributes.addAttribute("pageIndex", pageIndex + 1);
        redirectAttributes.addAttribute("pageSize", pageSize);
        redirectAttributes.addAttribute("openModal", userId);

        currentUser = userService.demote(userId, currentUser);

        return "redirect:/BGGForum/admin";
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @PostMapping("/{userId}/block")
    public String blockUser(@RequestParam(value = "pageIndex") int pageIndex,
                            @RequestParam(value = "pageSize") int pageSize,
                            @PathVariable long userId,
                            RedirectAttributes redirectAttributes,
                            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.get(userDetails.getUsername());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        redirectAttributes.addAttribute("pageIndex", pageIndex + 1);
        redirectAttributes.addAttribute("pageSize", pageSize);
        redirectAttributes.addAttribute("openModal", userId);

        userService.blockUser(userId, currentUser);

        return "redirect:/BGGForum/admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/archive")
    public String archiveUser(@RequestParam(value = "pageIndex") int pageIndex,
                              @RequestParam(value = "pageSize") int pageSize,
                              @RequestParam(value = "sectionId") String sectionId,
                              @PathVariable long userId,
                              RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        userService.softDelete(userId, currentUser);

        redirectAttributes.addAttribute("pageIndex", pageIndex + 1);
        redirectAttributes.addAttribute("pageSize", pageSize);
        redirectAttributes.addAttribute("openModal", userId);


        return "redirect:/BGGForum/admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/delete")
    public String deleteUser(@RequestParam(value = "pageIndex") int pageIndex,
                             @RequestParam(value = "pageSize") int pageSize,
                             @PathVariable long userId,
                             RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        userService.delete(userId, currentUser);

        redirectAttributes.addAttribute("pageIndex", pageIndex + 1);
        redirectAttributes.addAttribute("pageSize", pageSize);

        return "redirect:/BGGForum/admin";
    }
}
