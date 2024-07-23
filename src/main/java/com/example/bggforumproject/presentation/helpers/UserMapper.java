package com.example.bggforumproject.presentation.helpers;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.security.caseOne.LoginUserDTO;
import com.example.bggforumproject.security.caseOne.RegisterUserDTO;
import com.example.bggforumproject.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final UserService userService;

    public UserMapper(UserService userService) {
        this.userService = userService;
    }

    public User fromDTO(RegisterUserDTO dto) {
        User user = new User();

        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setUsername(dto.username());
        user.setPassword(dto.password());

        return user;
    }

    public User fromDTO(LoginUserDTO loginUserDTO){
        User user = userService.get(loginUserDTO.username());

        return user;
    }
}
