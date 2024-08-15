package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.helpers.filters.UserFilterOptions;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    User get(long id);

    User get(String username);

    Page<User> getAll(UserFilterOptions userFilterOptions, int pageIndex, int pageSize);

    Page<Post> getSpecificUserPosts(long id, PostFilterOptions postFilterOptions, int pageIndex, int pageSize);

    Page<Comment> getSpecificUserComments(long id, CommentFilterOptions commentFilterOptions, int pageIndex, int pageSize);

    User promote(long id, User currentUser);

    void delete(long id, User user);

    User update(long id, User loggedUser, User user);

    List<User> getAllModerators();

    List<User> getAll();

    void softDelete(long id, User currentUser);

    User blockUser(long id, User currentUser, boolean isBlocked);

    List<User> getAllAdmins();
}

