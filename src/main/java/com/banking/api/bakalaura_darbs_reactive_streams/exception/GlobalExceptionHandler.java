package com.banking.api.bakalaura_darbs_reactive_streams.exception;

import java.net.http.HttpTimeoutException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleNotFound(ResourceNotFoundException exception, ServerHttpRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getPath().value());
    }

    @ExceptionHandler({BadRequestException.class, FraudDetectedException.class})
    public Mono<ResponseEntity<ApiErrorResponse>> handleBadRequest(RuntimeException exception, ServerHttpRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getPath().value());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleExternalError(ExternalServiceException exception, ServerHttpRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY, exception.getMessage(), request.getPath().value());
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public Mono<ResponseEntity<String>> handleDatabaseTimeout(DataAccessResourceFailureException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE) // 503
                .body("Database connection pool is exhausted"));
    }

    @ExceptionHandler({
            ResourceAccessException.class,
            HttpTimeoutException.class,
            TimeoutException.class
    })
        public Mono<ResponseEntity<String>> handleExternalServiceTimeout(Exception ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT) // 504
            .body("External service did not respond in time"));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleGeneric(Exception exception, ServerHttpRequest request) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                request.getPath().value() + " " + exception.getMessage()
        );
    }

    private Mono<ResponseEntity<ApiErrorResponse>> buildResponse(HttpStatus status, String message, String path) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return Mono.just(ResponseEntity.status(status).body(body));
    }
}
