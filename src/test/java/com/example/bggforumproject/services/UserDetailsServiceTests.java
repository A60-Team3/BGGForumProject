package com.example.bggforumproject.services;

import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import com.example.bggforumproject.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTests {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void loadUserByUserName_Should_Throw_When_NoUser_Found(){
        Mockito
                .when(userRepository.getByUsername(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> userDetailsService.loadUserByUsername("string"));
    }

    @Test
    public void loadUserByUserName_Should_ReturnUserDetails_When_UserIsFound(){
        Mockito
                .when(userRepository.getByUsername(Mockito.anyString()))
                .thenReturn(Mockito.any(User.class));

        userDetailsService.loadUserByUsername("string");

        Mockito
                .verify(userRepository, Mockito.times(1))
                .getByUsername(Mockito.anyString());
        }


}
