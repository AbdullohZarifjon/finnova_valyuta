package com.example.finnovavalyutatask.exps.handler;

import com.example.finnovavalyutatask.exps.RecordAlreadyException;
import com.example.finnovavalyutatask.exps.RecordNotFoundException;
import com.example.finnovavalyutatask.payload.ApiResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ApiResponseFactory.error(HttpStatus.UNAUTHORIZED, "Username yoki password noto‘g‘ri");
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ApiResponseFactory.error(HttpStatus.FORBIDDEN, "Kirish rad etildi. Admin role kerak.");
    }

    @ExceptionHandler(RecordAlreadyException.class)
    public ResponseEntity<?> handleRecordAlreadyException(RecordAlreadyException ex) {
        return ApiResponseFactory.error(HttpStatus.CONFLICT, ex.getMessage());
    }


    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<?> handleRecordNotFoundException(RecordNotFoundException ex) {
        return ApiResponseFactory.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        return ApiResponseFactory.error(HttpStatus.BAD_REQUEST, "Validation xatosi", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        return ApiResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "Ichki tizim xatosi: " + ex.getMessage());
    }
}
