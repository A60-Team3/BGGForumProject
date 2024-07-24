package com.example.bggforumproject.presentation.helpers;

import com.example.bggforumproject.presentation.exceptions.AuthorizationException;
import com.example.bggforumproject.presentation.exceptions.CustomAuthenticationException;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.DateTimeException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("description", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleEntityNotFoundException(BadCredentialsException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("description", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<?> handleEntityNotFoundException(AccountStatusException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("description", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({CustomAuthenticationException.class})
    public ResponseEntity<?> handleAccessControlExceptions(CustomAuthenticationException ex ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleEntityNotFoundException(AccessDeniedException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("description", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<?> handleEntityNotFoundException(DateTimeException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("description", "Date provided is not valid.");
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> handleEntityNotFoundException(AuthorizationException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("description", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.FORBIDDEN);
    }
//
//    @ExceptionHandler(EntityDuplicateException.class)
//    public ResponseEntity<?> handleEntityDuplicateException(EntityDuplicateException ex) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Error", ex.getMessage());
//        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.NOT_FOUND);
//    }
}
