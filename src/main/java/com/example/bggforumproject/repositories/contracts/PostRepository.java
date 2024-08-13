package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostRepository extends OwnerRepository<Post>{

    Page<Post> get(PostFilterOptions postFilterOptions, int pageIndex, int pageSize);
    List<Post> get();

    Post get(long id);

    Post get(String title);

    void create(Post post);

    void update(Post post);

    void delete(Post post);

    List<Post> getMostCommented();

    List<Post> getMostRecentlyCreated();


}
