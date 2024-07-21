package com.example.bggforumproject.presentation.helpers;

import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Error", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.NOT_FOUND);
    }
//
//    @ExceptionHandler({AuthenticationException.class, AuthorizationException.class})
//    public ResponseEntity<?> handleAccessControlExceptions(RuntimeException ex ) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Access-Control", ex.getMessage());
//        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.UNAUTHORIZED);
//    }
//
//    @ExceptionHandler(EntityDuplicateException.class)
//    public ResponseEntity<?> handleEntityDuplicateException(EntityDuplicateException ex) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Error", ex.getMessage());
//        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.NOT_FOUND);
//    }
}
