package com.example.bggforumproject.service.impl;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User get(long id) {
        return null;
    }

    @Override
    public User get(String username) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return List.of();
    }
}
