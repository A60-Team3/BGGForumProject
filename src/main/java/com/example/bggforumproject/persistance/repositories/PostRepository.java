package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PostRepository {

    List<Post> get(PostFilterOptions postFilterOptions);

    Post get(long id);

    Post get(String title);

    void create(Post post);

    void update(Post post);

    void delete(long id);

    List<Post> getMostCommented();

    List<Post> getMostRecentlyCreated();
}
