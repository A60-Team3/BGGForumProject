package com.example.bggforumproject.service;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.presentation.dtos.PostOutFullDTO;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;

import java.util.List;

public interface AnonymousUserService {
    long countUsers();

    long countPosts();

    List<Post> getAllPosts(PostFilterOptions postFilterOptions);
}
