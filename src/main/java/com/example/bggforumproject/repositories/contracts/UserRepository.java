package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.User;
import com.example.bggforumproject.helpers.filters.UserFilterOptions;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    List<User> getAll(UserFilterOptions userFilterOptions);

    User getById(long id);

    User getByUsername(String username);

    User getByEmail(String email);

    void create(User user);

    void update(User user);

    void delete(User user);
}
