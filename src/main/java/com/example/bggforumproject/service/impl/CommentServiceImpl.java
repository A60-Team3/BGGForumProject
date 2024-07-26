package com.example.bggforumproject.service.impl;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.CommentRepository;
import com.example.bggforumproject.persistance.repositories.PostRepository;
import com.example.bggforumproject.presentation.exceptions.AuthorizationException;
import com.example.bggforumproject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private static final String MODIFY_COMMENT_ERROR_MESSAGE = "Comments can only be modified by their creator!";
    private static final String DELETE_COMMENT_ERROR_MESSAGE = "Comments can only be deleted by their creator, moderators or admins!";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
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
        checkModifyPermissions(comment.getId(), user);

        commentRepository.update(comment);
    }

    @Override
    public void delete(long id, User user) {
        checkDeletePermissions(id, user);

        commentRepository.delete(id);
    }

    private void checkModifyPermissions(long commentId, User user) {
        Comment comment = commentRepository.get(commentId);
        if (comment.getUserId().getId() != user.getId()) {
            throw new AuthorizationException(MODIFY_COMMENT_ERROR_MESSAGE);
        }
    }

    private void checkDeletePermissions(long commentId, User user) {
        Comment comment = commentRepository.get(commentId);
        boolean isRegularUser = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER"));
        if (isRegularUser) {
            if (user.getId() != comment.getUserId().getId()) {
                throw new AuthorizationException(DELETE_COMMENT_ERROR_MESSAGE);
            }
        }
    }
}
