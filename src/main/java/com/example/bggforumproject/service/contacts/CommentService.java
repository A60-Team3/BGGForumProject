package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.User;

import java.util.List;

public interface CommentService {

    List<Comment> getAll(CommentFilterOptions commentFilterOptions);

    List<Comment> getCommentsForPost(long postId, CommentFilterOptions commentFilterOptions);

    Comment get(long id);

    Comment create(long id, Comment comment, User user);

    Comment update(long commentId, long postId, String comment, User user);

    void delete(long id, long postId, User user);

}
