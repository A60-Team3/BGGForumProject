package com.example.bggforumproject.presentation.helpers;

import com.example.bggforumproject.presentation.dtos.ApiErrorResponseDTO;
import com.example.bggforumproject.presentation.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        ex.getBindingResult()
                .getGlobalErrors()
                .forEach(objectError -> errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage()));

        ApiErrorResponseDTO apiErrorResponseDTO = new ApiErrorResponseDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<>(apiErrorResponseDTO, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("description", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityDuplicateException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityDuplicateException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("description", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.CONFLICT);
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
    public ResponseEntity<?> handleAccessControlExceptions(CustomAuthenticationException ex) {
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

    @ExceptionHandler(IllegalUsernameModificationException.class)
    public ResponseEntity<?> handleEntityNotFoundException(IllegalUsernameModificationException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("description", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.NOT_ACCEPTABLE);
    }
}
