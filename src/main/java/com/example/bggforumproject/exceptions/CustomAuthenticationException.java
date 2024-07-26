package com.example.bggforumproject.exceptions;

public class CustomAuthenticationException extends RuntimeException{
    public CustomAuthenticationException(String message) {
        super(message);
    }
}
