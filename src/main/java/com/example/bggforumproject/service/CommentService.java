package com.example.bggforumproject.service;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.helpers.CommentFilterOptions;

import java.util.List;

public interface CommentService {

    List<Comment> getCommentsForPost(long postId);

    Comment get(long id);

    void create(Comment comment, User user);

    void update(Comment comment, User user);

    void delete(long id, User user);

}
