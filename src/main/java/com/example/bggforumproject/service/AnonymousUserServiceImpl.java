package com.example.bggforumproject.service;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import com.example.bggforumproject.helpers.PostFilterOptions;
import com.example.bggforumproject.service.contacts.AnonymousUserService;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return postRepository.get().size();
    }

    @Override
    public List<Post> getAllPosts(PostFilterOptions postFilterOptions) {
        return postRepository.get(postFilterOptions);
    }
}
