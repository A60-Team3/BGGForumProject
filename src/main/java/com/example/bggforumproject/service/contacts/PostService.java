package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.helpers.PostFilterOptions;

import java.util.List;

public interface PostService {

    List<Post> get(PostFilterOptions postFilterOptions);

    Post get(long id);

    Post get(String title);

    void create(Post post, User user);

    void update(Post post, User user);

    void delete(long id, User user);

    List<Post> getMostCommented();

    List<Post> getMostRecentlyCreated();
}
