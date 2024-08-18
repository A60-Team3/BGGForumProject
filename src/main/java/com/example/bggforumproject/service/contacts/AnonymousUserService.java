package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AnonymousUserService {
    long countUsers();

    long countPosts();

    Page<Post> getAllPosts(PostFilterOptions postFilterOptions, int pageIndex, int pageSize);

    List<Post> getMostCommented();

    Page<Post> getMostRecentlyCreated(int pageIndex, int pageSize);

    User get(String username);
}
