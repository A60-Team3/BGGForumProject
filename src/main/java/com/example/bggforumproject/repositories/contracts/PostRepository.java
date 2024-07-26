package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.helpers.PostFilterOptions;

import java.util.List;

public interface PostRepository extends OwnerRepository<Post>{

    List<Post> get(PostFilterOptions postFilterOptions);
    List<Post> get();

    Post get(long id);

    Post get(String title);

    void create(Post post);

    void update(Post post);

    void delete(long id);

    List<Post> getMostCommented();

    List<Post> getMostRecentlyCreated();


}
