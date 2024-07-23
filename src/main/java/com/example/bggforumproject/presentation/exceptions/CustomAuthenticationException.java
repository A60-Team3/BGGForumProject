package com.example.bggforumproject.presentation.exceptions;

public class CustomAuthenticationException extends RuntimeException{
    public CustomAuthenticationException(String message) {
        super(message);
    }
}
