package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            com.example.bggforumproject.models.User user = userRepository.getByUsername(username);
            return User.builder()
                    .username(username)
                    .password(user.getPassword())
                    .credentialsExpired(false)
                    .accountExpired(user.isDeleted())
                    .accountLocked(user.isBlocked())
                    .disabled(user.isBlocked())
                    .authorities(user.getRoles()
                            .stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getAuthority()))
                            .collect(Collectors.toList()))
                    .build();
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
