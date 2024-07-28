package com.example.bggforumproject.service;


import com.example.bggforumproject.dtos.ResponseDTO;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.RoleRepository;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import com.example.bggforumproject.security.TokenService;
import com.example.bggforumproject.service.contacts.AuthenticationService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthorizationHelper authorizationHelper;


    public AuthenticationServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                                     PasswordEncoder passwordEncoder, TokenService tokenService,
                                     AuthorizationHelper authorizationHelper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authorizationHelper = authorizationHelper;
    }

    @Override
    public User registerUser(User user) {
        authorizationHelper.validateEmailIsUnique(user.getId(), user.getEmail());

        try{
            userRepository.getByUsername(user.getUsername());
            throw new EntityDuplicateException("User","username", user.getUsername());
        } catch (EntityNotFoundException ignored){

        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        Role userRole = roleRepository.getByAuthority(RoleType.USER.name());

        user.setPassword(encodedPassword);
        user.setRoles(Set.of(userRole));

        return userRepository.create(user);
    }

    @Override
    public ResponseDTO loginUser(Authentication auth) {

        String token = tokenService.generateJwt(auth);
        User user = (User) auth.getPrincipal();

        return new ResponseDTO(user.getId(), user.getUsername(), token);
    }
}
