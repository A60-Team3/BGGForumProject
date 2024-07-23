package com.example.bggforumproject.service.impl;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.dtos.UserOutDTO;
import com.example.bggforumproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserOutDTO get(long id) {
        User user = userRepository.findById(id);

        return mapper.map(user, UserOutDTO.class);
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
