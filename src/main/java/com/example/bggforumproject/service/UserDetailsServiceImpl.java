package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userRepository.getByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
