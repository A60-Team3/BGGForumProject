package com.example.bggforumproject.exceptions;

public class IllegalFileUploadException extends RuntimeException{
    public IllegalFileUploadException(String message) {
        super(message);
    }
}
