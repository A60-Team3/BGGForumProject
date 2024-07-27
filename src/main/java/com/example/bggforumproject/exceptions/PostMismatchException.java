package com.example.bggforumproject.exceptions;

public class PostMismatchException extends RuntimeException {

    public PostMismatchException(String type, String typeAttribute) {
        super(String.format("No %s \"%s\" found at this post", type, typeAttribute));
    }
}
