package com.example.bggforumproject.exceptions;

public class IllegalUsernameModificationException extends RuntimeException{

    public IllegalUsernameModificationException() {
        super("Username cannot be changed");
    }
}
