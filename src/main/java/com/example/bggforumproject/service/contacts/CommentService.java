package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.User;
import org.springframework.data.domain.Page;

public interface CommentService {

    Page<Comment> getAll(CommentFilterOptions commentFilterOptions);

    Page<Comment> getCommentsForPost(long postId, CommentFilterOptions commentFilterOptions, int pageIndex, int pageSize);

    Comment get(long id);

    Comment create(long id, Comment comment, User user);

    Comment update(long commentId, long postId, String comment, User user);

    void delete(long id, long postId, User user);

}
