package com.example.bggforumproject.security.caseTwo;


import com.example.bggforumproject.persistance.models.Role;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.models.enums.RoleType;
import com.example.bggforumproject.persistance.repositories.RoleRepository;
import com.example.bggforumproject.persistance.repositories.UserRepository;

import com.example.bggforumproject.security.caseOne.LoginUserDTO;
import com.example.bggforumproject.security.caseOne.RegisterUserDTO;
import com.example.bggforumproject.service.AuthenticationService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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


    public AuthenticationServiceImplTwo(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public User registerUser(String firstName, String lastName, String email, String password , String username){

        String encodedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByAuthority(RoleType.USER.name());

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRoles(Set.of(userRole));

        return userRepository.create(user);
    }

    public ResponseDTO loginUser(String username, String password){

        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJwt(auth);

            return new ResponseDTO((User) auth.getPrincipal(), token);

        } catch(AuthenticationException e){
            return new ResponseDTO(null, "");
        }
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
