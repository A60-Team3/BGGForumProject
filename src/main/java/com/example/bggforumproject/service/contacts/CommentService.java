package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.User;

import java.util.List;

public interface CommentService {

    List<Comment> getCommentsForPost(long postId);

    Comment get(long id);

    void create(Comment comment, User user);

    void update(Comment comment, User user);

    void delete(long id, User user);

}
