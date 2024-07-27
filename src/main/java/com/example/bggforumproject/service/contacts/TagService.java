package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.helpers.filters.TagFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.models.User;

import java.util.List;

public interface TagService {

    List<Tag> get(TagFilterOptions tagFilterOptions);

    Tag get(long id);

    Tag get(String name);

    void addTagToPost(Tag tag, Post post);

    void deleteTagFromPost(long tagId, long postId, User user);

    void create(Tag tag);

    void update(Tag tag, User user);

    void delete(long id, User user);
}
