package com.example.bggforumproject.service.impl;

import com.example.bggforumproject.persistance.repositories.PostRepository;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.service.AnonymousUserService;
import org.springframework.stereotype.Service;

@Service
public class AnonymousUserServiceImpl implements AnonymousUserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public AnonymousUserServiceImpl(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public long countUsers() {
        return userRepository.findAll().size();
    }

    @Override
    public long countPosts() {
        return postRepository.get(
                new PostFilterOptions(null, null, null, null, null, null, null, null))
                .size();
    }
}
