package com.example.bggforumproject.service;

import com.example.bggforumproject.persistance.models.User;

import java.util.List;

public interface UserService {
    User get(long id);

    User get(String username);

    List<User> getAll();
}
