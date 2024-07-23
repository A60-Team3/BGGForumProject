package com.example.bggforumproject.security.caseOne;


import com.example.bggforumproject.persistance.models.Role;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.models.enums.RoleType;
import com.example.bggforumproject.persistance.repositories.RoleRepository;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.security.caseTwo.RegistrationDTO;
import com.example.bggforumproject.security.caseTwo.ResponseDTO;
import com.example.bggforumproject.service.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;


public class AuthenticationServiceImplOne implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper mapper;


    public AuthenticationServiceImplOne(UserRepository userRepository, RoleRepository roleRepository,
                                        PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                                        ModelMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
    }

    @Override
    public User registerUser(RegisterUserDTO input) {
        User user = mapper.map(input, User.class);

        Role role = roleRepository.findByAuthority(RoleType.USER.name());

        user.setPassword(passwordEncoder.encode(input.password()));
        user.setRoles(Set.of(role));

        return userRepository.create(user);
    }

    @Override
    public User loginUser(LoginUserDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.email(),
                        input.password()
                )
        );

        return userRepository.findByEmail(input.email());
    }

    @Override
    public ResponseDTO registerUser(RegistrationDTO dto) {
        return null;
    }

    @Override
    public ResponseDTO loginUser(Authentication auth) {
        return null;
    }


}
