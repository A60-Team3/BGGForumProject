package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.presentation.helpers.CommentFilterOptions;

import java.util.List;

public interface CommentRepository {

    List<Comment> get(CommentFilterOptions commentFilterOptions);

    List<Comment> getCommentsForPost(long postId);

    Comment get(long id);

    void create(Comment comment);

    void update(Comment comment);

    void delete(long id);
}
