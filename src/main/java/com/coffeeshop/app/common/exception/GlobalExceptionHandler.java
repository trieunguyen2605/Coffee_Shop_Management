package com.coffeeshop.app.common.exception;

import com.coffeeshop.app.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation Error");
        response.setData(errors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 2. Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 3. Handle custom business logic exceptions
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getStatus().value(), ex.getMessage());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    // 4. Handle Access Denied (Bắt lỗi khi @PreAuthorize chặn lại)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Forbidden: Bạn không có quyền thực hiện hành động này");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 5. Handle Endpoint Not Found (Spring Boot 3.2+ NoResourceFoundException)
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Endpoint không tồn tại: " + ex.getResourcePath());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 5. Handle unexpected generic exceptions (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
