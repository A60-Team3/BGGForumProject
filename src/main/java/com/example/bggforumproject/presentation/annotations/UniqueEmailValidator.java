package com.example.bggforumproject.presentation.annotations;

import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail,String> {

    private final UserRepository userRepository;

public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            userRepository.findByEmail(value);
            return false;
        } catch (EntityNotFoundException exception) {
            return true;
        }
    }
}
