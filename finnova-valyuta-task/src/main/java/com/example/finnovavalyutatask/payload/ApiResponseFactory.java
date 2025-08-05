package com.example.finnovavalyutatask.payload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseFactory {

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<ApiResponse<Void>> success(String message) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }


    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success("Success", data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success() {
        return success("Success", null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message, T data) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message, T data) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message) {
        return error(status, message, null);
    }

}
