package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.Tag;

import java.util.List;

public interface TagRepository {

    List<Tag> get();

    Tag get(long id);

    Tag get(String name);

    List<Tag> getTagsOfPost(long postId);

    void create(Tag tag);

    void addTagToPost(Tag tag, Post post);

    void update(Tag tag);

    void delete(long id);

}
