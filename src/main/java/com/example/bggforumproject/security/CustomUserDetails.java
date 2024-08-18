package com.example.bggforumproject.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.stream.Collectors;

public class CustomUserDetails extends User {
    private final Long id;

    public CustomUserDetails(com.example.bggforumproject.models.User user) {
        super(user.getUsername(), user.getPassword(),
                !user.isBlocked(), !user.isDeleted(),
                true, !user.isBlocked(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
                        .collect(Collectors.toList())
        );

        this.id = user.getId();
    }

    public Long getId() {
        return id;
    }
}
