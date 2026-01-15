package com.cloud.order_service.infrastructure.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cloud.order_service.common.exception.BaseException;
import com.cloud.order_service.common.exception.ErrorResponse;
import com.cloud.order_service.common.exception.GlobalErrorCode;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
        BaseException ex,
        HttpServletRequest request
    ){
        return new ResponseEntity<>(
            ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(Instant.now().toString())
                .build(),
            ex.getHttpStatus()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handAllException(
        Exception ex,
        HttpServletRequest request
    ){
        return new ResponseEntity<>(
            ErrorResponse.builder()
                .code(GlobalErrorCode.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(Instant.now().toString())
                .build(),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
