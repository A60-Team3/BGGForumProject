package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.helpers.filters.UserFilterOptions;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User get(long id);

    User get(String username);

    List<User> getAll();

    Page<User> getAll(UserFilterOptions userFilterOptions, int pageIndex, int pageSize);

    List<User> getAllModerators();

    List<User> getAllAdmins();

    Page<Post> getSpecificUserPosts(long id, int pageIndex, int pageSize);

    Page<Comment> getSpecificUserComments(long id, int pageIndex, int pageSize);

    User update(long id, User loggedUser, User user);

    User promote(long id, User currentUser);

    User blockUser(long id, User currentUser);

    void softDelete(long id, User currentUser);

    void delete(long id, User user);

    void uploadProfilePic(MultipartFile multipartFile, User user, long userId) throws IOException;
}

