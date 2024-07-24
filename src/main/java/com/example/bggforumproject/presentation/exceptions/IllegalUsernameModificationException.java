package com.example.bggforumproject.presentation.exceptions;

public class IllegalUsernameModificationException extends RuntimeException{

    public IllegalUsernameModificationException() {
        super("Username cannot be changed");
    }
}
