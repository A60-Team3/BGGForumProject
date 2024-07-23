package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.User;

import java.util.List;

public interface UserRepository {
    User findByUsername(String username);

    List<User> findAll();

    User create(User user);

    User findByEmail(String email);

    User findById(long id);
}
