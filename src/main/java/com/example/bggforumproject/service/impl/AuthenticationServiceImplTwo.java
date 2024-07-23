package com.example.bggforumproject.service.impl;


import com.example.bggforumproject.persistance.models.Role;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.models.enums.RoleType;
import com.example.bggforumproject.persistance.repositories.RoleRepository;
import com.example.bggforumproject.persistance.repositories.UserRepository;

import com.example.bggforumproject.security.caseOne.LoginUserDTO;
import com.example.bggforumproject.security.caseOne.RegisterUserDTO;
import com.example.bggforumproject.security.caseTwo.RegistrationDTO;
import com.example.bggforumproject.security.caseTwo.ResponseDTO;
import com.example.bggforumproject.security.caseTwo.TokenService;
import com.example.bggforumproject.service.AuthenticationService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Set;

@Service
@Transactional
public class AuthenticationServiceImplTwo implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ModelMapper mapper;


    public AuthenticationServiceImplTwo(UserRepository userRepository, RoleRepository roleRepository,
                                        PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                                        TokenService tokenService, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.mapper = mapper;
    }

    @Override
    public ResponseDTO registerUser(RegistrationDTO dto) {

        String encodedPassword = passwordEncoder.encode(dto.password());
        Role userRole = roleRepository.findByAuthority(RoleType.USER.name());

        User user = mapper.map(dto, User.class);

        user.setPassword(encodedPassword);
        user.setRoles(Set.of(userRole));

        User created = userRepository.create(user);

        return mapper.map(created, ResponseDTO.class);
    }

    @Override
    public ResponseDTO loginUser(Authentication auth) {

        String token = tokenService.generateJwt(auth);
        User user = (User) auth.getPrincipal();

        return new ResponseDTO(user.getId(), user.getUsername(), token);

    }

    //One
    @Override
    public User registerUser(RegisterUserDTO user) {
        return null;
    }

    //One
    @Override
    public User loginUser(LoginUserDTO input) {
        return null;
    }
}
