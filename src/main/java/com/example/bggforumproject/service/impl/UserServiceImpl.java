package com.example.bggforumproject.service.impl;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.CommentRepository;
import com.example.bggforumproject.persistance.repositories.PostRepository;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.helpers.CommentFilterOptions;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.presentation.helpers.UserFilterOptions;
import com.example.bggforumproject.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserServiceImpl(UserRepository userRepository, PostRepository postRepository,
                           CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public User get(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User get(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAll(UserFilterOptions userFilterOptions) {
        return userRepository.findAll(userFilterOptions);
    }

    @Override
    public List<Post> getSpecificUserPosts(PostFilterOptions postFilterOptions) {
        return postRepository.get(postFilterOptions);
    }

    @Override
    public List<Comment> getSpecificUserComments(CommentFilterOptions commentFilterOptions) {
        return commentRepository.get(commentFilterOptions);
    }
}
