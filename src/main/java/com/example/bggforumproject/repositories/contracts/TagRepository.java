package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Tag;

import java.util.List;

public interface TagRepository {

    List<Tag> get();

    Tag get(long id);

    Tag get(String name);

    List<Tag> getTagsOfPost(long postId);

    void create(Tag tag);

    void update(Tag tag);

    void delete(long id);

}
