package com.example.bggforumproject.service.impl;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User get(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User get(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
