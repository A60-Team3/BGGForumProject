package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;

import java.util.List;

public interface CommentRepository extends OwnerRepository<Comment>{

    List<Comment> get(CommentFilterOptions commentFilterOptions);

    List<Comment> getCommentsForPost(long postId);

    Comment get(long id);

    void create(Comment comment);

    void update(Comment comment);

    void delete(long id);
}
