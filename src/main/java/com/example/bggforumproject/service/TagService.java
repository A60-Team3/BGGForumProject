package com.example.bggforumproject.service;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.Tag;
import com.example.bggforumproject.persistance.models.User;

import java.util.List;

public interface TagService {

    List<Tag> get();

    Tag get(long id);

    Tag get(String name);

    void addTagToPost(Tag tag, Post post);

    void deleteTagFromPost(long tagId, Post post, User user);

    void create(Tag tag);

    void update(Tag tag, User user);

    void delete(long id, User user);
}
