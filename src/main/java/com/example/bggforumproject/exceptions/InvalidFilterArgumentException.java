package com.example.bggforumproject.exceptions;

public class InvalidFilterArgumentException extends RuntimeException{
    public InvalidFilterArgumentException() {
    }

    public InvalidFilterArgumentException(String message) {
        super(message);
    }
}
