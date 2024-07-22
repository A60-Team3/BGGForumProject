package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.presentation.helpers.CommentFilterOptions;

import java.util.List;

public interface CommentRepository {

    List<Comment> get(CommentFilterOptions commentFilterOptions);

    Comment get(int id);

    void create(Comment comment);

    void update(Comment comment);

    void delete(int id);
}
