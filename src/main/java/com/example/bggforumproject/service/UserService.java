package com.example.bggforumproject.service;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.helpers.CommentFilterOptions;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;

import java.util.List;

public interface UserService {
    User get(long id);

    User get(String username);

    List<User> getAll();

    List<Post> getSpecificUserPosts(PostFilterOptions postFilterOptions);

    List<Comment> getSpecificUserComments(CommentFilterOptions commentFilterOptions);

}
