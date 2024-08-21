package com.example.bggforumproject.services;

import com.example.bggforumproject.dtos.response.ResponseDTO;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.RoleRepository;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import com.example.bggforumproject.service.AuthenticationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static com.example.bggforumproject.helpers.CreationHelper.createMockUser;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTests {

    @InjectMocks
    private AuthenticationServiceImpl service;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthorizationHelper authorizationHelper;

    @Test
    public void registerUser_Should_Throw_When_EmailIsNotUnique() {
        User user = createMockUser();

        Mockito
                .doThrow(EntityDuplicateException.class)
                .when(authorizationHelper)
                .validateEmailIsUnique(Mockito.anyLong(), Mockito.anyString());

        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.registerUser(user)
        );
    }

    @Test
    public void registerUser_Should_Throw_When_UsernameIsNotUnique() {
        User user = createMockUser();

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .validateEmailIsUnique(Mockito.anyLong(), Mockito.anyString());

        Mockito.when(userRepository.getByUsername(Mockito.anyString()))
                        .thenReturn(user);

        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.registerUser(user)
        );
    }

    @Test
    public void registerUser_Should_Return_RegisteredUser() {
        User user = createMockUser();

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .validateEmailIsUnique(Mockito.anyLong(), Mockito.anyString());

        Mockito.when(userRepository.getByUsername(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        Mockito
                .when(passwordEncoder.encode(Mockito.anyString()))
                .thenReturn(user.getPassword());

        Mockito.when(userRepository.getAll())
                .thenReturn(List.of(user));

        Mockito
                .when(roleRepository.getByAuthority(Mockito.any()))
                .thenReturn(new Role(RoleType.USER));

//        service.registerUser(user);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.registerUser(user)
        );

        Mockito.verify(userRepository, Mockito.times(1)).create(user);
    }

    @Test
    public void loginUser_Should_Return_JWTToken() {
        User user = createMockUser();
        Authentication auth = new TestingAuthenticationToken(user, "password123");

        ResponseDTO responseDTO = service.loginUser(auth);

        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getId(), responseDTO.getId()),
                () -> Assertions.assertEquals(user.getUsername(), responseDTO.getUsername()),
                () -> Assertions.assertEquals(user.getPassword(), responseDTO.getJwt())
        );


    }

}
