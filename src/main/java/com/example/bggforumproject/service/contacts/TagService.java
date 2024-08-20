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

    Post addTagToPost(long id, String tag, User user);

    void deleteTagFromPost(long tagId, long postId, User user);

    void create(Tag tag);

    void delete(long id, User user);
}
