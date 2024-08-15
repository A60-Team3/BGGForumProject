package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.PostMismatchException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.CommentRepository;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.service.contacts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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
    public Page<Comment> getAll(CommentFilterOptions commentFilterOptions) {
        return commentRepository.get(commentFilterOptions, 0, 1);
    }

    @Override
    public Page<Comment> getCommentsForPost(long postId, int pageIndex, int pageSize) {
        postRepository.get(postId);
        CommentFilterOptions commentFilterOptions = new CommentFilterOptions
                (
                        null, null, null, null,
                        postId, null, null
                );

        return commentRepository.get(commentFilterOptions, pageIndex, pageSize);
    }

    @Override
    public Comment get(long id) {
        return commentRepository.get(id);
    }

    @Override
    public Comment create(long id, Comment comment, User user) {
        Post post = postRepository.get(id);
        comment.setPostId(post);
        comment.setUserId(user);

        commentRepository.create(comment);

        return comment;
    }

    @Override
    public Comment update(long commentId, long postId, String updatedContent, User user) {
        Comment repoComment = commentRepository.get(commentId);
        Post post = postRepository.get(postId);

        if (!repoComment.getPostId().equals(post)) {
            throw new PostMismatchException("Comment does not belong to the specified post");
        }

        authorizationHelper.checkOwnership(repoComment, user);

        if (repoComment.getContent().equals(updatedContent)) {
            throw new EntityDuplicateException("Comment content must be different");
        }

        repoComment.setContent(updatedContent);

        commentRepository.update(repoComment);

        return repoComment;
    }

    @Override
    public void delete(long id, long postId, User user) {
        Comment repoComment = commentRepository.get(id);
        Post post = postRepository.get(postId);

        if (!repoComment.getPostId().equals(post)) {
            throw new PostMismatchException("Comment does not belong to the specified post");
        }

        try {
            authorizationHelper.checkPermissionsAndOwnership(repoComment, user, RoleType.ADMIN, RoleType.MODERATOR);
        } catch (AuthorizationException e) {
            throw new AuthorizationException(DELETE_COMMENT_ERROR_MESSAGE);
        }

        commentRepository.delete(id);
    }
}
