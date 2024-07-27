package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.helpers.CommentFilterOptions;
import com.example.bggforumproject.helpers.PostFilterOptions;
import com.example.bggforumproject.helpers.UserFilterOptions;

import java.util.List;

public interface UserService {
    User get(long id);

    User get(String username);

    List<User> getAll(UserFilterOptions userFilterOptions);

    List<Post> getSpecificUserPosts(PostFilterOptions postFilterOptions);

    List<Comment> getSpecificUserComments(CommentFilterOptions commentFilterOptions);

    User promote(long id, User currentUser);

    void delete(long id, User user);

    User update(long id, User loggedUser, User user);

    void softDelete(long id, User currentUser);

    void blockUser(long id, User currentUser, boolean dto);
}

