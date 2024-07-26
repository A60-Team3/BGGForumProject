package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.User;
import com.example.bggforumproject.helpers.UserFilterOptions;

import java.util.List;

public interface UserRepository {
    User findByUsername(String username);

    List<User> findAll();

    List<User> findAll(UserFilterOptions userFilterOptions);

    User create(User user);

    User findByEmail(String email);

    User findById(long id);

    void update(User user);

    void delete(long id);
}
