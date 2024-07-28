package com.example.bggforumproject.helpers;

import com.example.bggforumproject.dtos.ApiErrorResponseDTO;
import com.example.bggforumproject.exceptions.*;
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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

        ApiErrorResponseDTO apiErrorResponseDTO = new ApiErrorResponseDTO(HttpStatus.BAD_REQUEST, ex.getMessage(), errors);
        return new ResponseEntity<>(apiErrorResponseDTO, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), List.of(ex.getMessage()));

        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityDuplicateException.class)
    public ResponseEntity<?> handleEntityDuplicateException(EntityDuplicateException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.CONFLICT, ex.getLocalizedMessage(), List.of(ex.getMessage()));

        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<?> handleAccountStatusException(AccountStatusException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({CustomAuthenticationException.class})
    public ResponseEntity<?> handleCustomAuthenticationException(CustomAuthenticationException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<?> handleDateTimeException(DateTimeException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostMismatchException.class)
    public ResponseEntity<?> handleDateTimeException(PostMismatchException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFilterArgumentException.class)
    public ResponseEntity<?> handleDateTimeException(InvalidFilterArgumentException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> handleAuthorizationException(AuthorizationException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalUsernameModificationException.class)
    public ResponseEntity<?> handleIllegalUsernameModificationException(IllegalUsernameModificationException ex) {
        ApiErrorResponseDTO apiErrorResponseDTO =
                new ApiErrorResponseDTO(HttpStatus.NOT_ACCEPTABLE, ex.getLocalizedMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponseDTO, HttpStatus.NOT_ACCEPTABLE);
    }
}
