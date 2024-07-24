package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.helpers.UserFilterOptions;

import java.util.List;

public interface UserRepository {
    User findByUsername(String username);

    List<User> findAll();

    List<User> findAll(UserFilterOptions userFilterOptions);

    User create(User user);

    User findByEmail(String email);

    User findById(long id);
}
