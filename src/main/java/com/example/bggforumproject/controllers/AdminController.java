package com.example.bggforumproject.controllers;

import com.example.bggforumproject.models.User;
import com.example.bggforumproject.dtos.BlockDTO;
import com.example.bggforumproject.dtos.UserOutDTO;
import com.example.bggforumproject.service.contacts.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/BGGForum/admin")
public class AdminController {
    private final UserService userService;
    private final ModelMapper mapper;

    public AdminController(UserService userService, ModelMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PutMapping("/{id}")
    public void blockUser(@PathVariable long id, @RequestBody boolean isBlocked) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        userService.blockUser(id, currentUser, isBlocked);
    }


    @PutMapping("/admin/{id}")
    public ResponseEntity<UserOutDTO> promoteUser(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        User user = userService.promote(id, currentUser);
        UserOutDTO dto = mapper.map(user, UserOutDTO.class);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/admin/{id}/archive")
    public void archiveUser(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        userService.softDelete(id, currentUser);
    }

    @DeleteMapping("/admin/{id}")
    public void deleteUser(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.get(authentication.getName());

        userService.delete(id, currentUser);
    }
}
