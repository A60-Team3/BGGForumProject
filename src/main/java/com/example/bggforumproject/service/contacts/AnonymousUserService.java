package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.helpers.PostFilterOptions;

import java.util.List;

public interface AnonymousUserService {
    long countUsers();

    long countPosts();

    List<Post> getAllPosts(PostFilterOptions postFilterOptions);
}
