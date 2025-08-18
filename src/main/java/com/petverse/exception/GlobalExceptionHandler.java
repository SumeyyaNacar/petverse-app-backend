package com.petverse.exception;


import com.petverse.payload.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Ortak hata response'u oluşturan metod
    private ResponseEntity<ResponseError> buildErrorResponse(
            HttpStatus status, String error, String message) {

        ResponseError responseError = new ResponseError(
                status.value(),
                error,
                message,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(responseError, status);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseError> handleConflictException(ConflictException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseError> handleBadRequestException(BadRequestException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(NoEntityFoundException.class)
    public ResponseEntity<ResponseError> handleResourceNotFoundException(NoEntityFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Entity Not Found", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseError> accessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage());
    }

    // Beklenmeyen tüm hatalar için genel handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }
}
