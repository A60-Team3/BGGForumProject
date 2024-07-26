package com.example.bggforumproject.service;

import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.CommentRepository;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.service.contacts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private static final String DELETE_COMMENT_ERROR_MESSAGE = "Comments can only be deleted by their creator, moderators or admins!";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final AuthorizationHelper authorizationHelper;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, AuthorizationHelper authorizationHelper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.authorizationHelper = authorizationHelper;
    }


    @Override
    public List<Comment> getCommentsForPost(long postId) {
        postRepository.get(postId);
        return commentRepository.getCommentsForPost(postId);
    }

    @Override
    public Comment get(long id) {
        return commentRepository.get(id);
    }

    @Override
    public void create(Comment comment, User user) {
        comment.setUserId(user);
        commentRepository.create(comment);
    }

    @Override
    public void update(Comment comment, User user) {
        authorizationHelper.checkOwnership(comment.getId(), user, commentRepository);

        commentRepository.update(comment);
    }

    @Override
    public void delete(long id, User user) {
        try {
            authorizationHelper.checkPermissionsAndOwnership(id, user, commentRepository, "ADMIN", "MODERATOR");
        } catch (AuthorizationException e) {
            throw new AuthorizationException(DELETE_COMMENT_ERROR_MESSAGE);
        }

        commentRepository.delete(id);
    }
}
