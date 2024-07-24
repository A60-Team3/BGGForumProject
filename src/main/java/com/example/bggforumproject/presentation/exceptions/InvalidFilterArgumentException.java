package com.example.bggforumproject.presentation.exceptions;

public class InvalidFilterArgumentException extends RuntimeException{
    public InvalidFilterArgumentException() {
    }

    public InvalidFilterArgumentException(String message) {
        super(message);
    }
}
