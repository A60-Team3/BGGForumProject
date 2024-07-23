package com.example.bggforumproject.service;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.dtos.UserOutDTO;

import java.util.List;

public interface UserService {
    UserOutDTO get(long id);

    User get(String username);

    List<User> getAll();
}
