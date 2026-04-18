package com.lbg.exception;

import com.lbg.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ────────────────────────────────────────────────────────
    @ExceptionHandler(CaseNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCaseNotFound(
            CaseNotFoundException ex, HttpServletRequest request) {

        log.warn("Case not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request, null);
    }

    // ── 409 Conflict ─────────────────────────────────────────────────────────
    @ExceptionHandler(DuplicateCaseException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateCase(
            DuplicateCaseException ex, HttpServletRequest request) {

        log.warn("Duplicate case: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request, null);
    }

    // ── 422 Unprocessable Entity — stage skipping ─────────────────────────────
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTransition(
            InvalidStatusTransitionException ex, HttpServletRequest request) {

        log.warn("Invalid status transition: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage(), request, null);
    }

    // ── 422 Unprocessable Entity — SAR from wrong status ─────────────────────
    @ExceptionHandler(SarNotAllowedException.class)
    public ResponseEntity<ApiErrorResponse> handleSarNotAllowed(
            SarNotAllowedException ex, HttpServletRequest request) {

        log.warn("SAR not allowed: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage(), request, null);
    }

    // ── 400 Bad Request — Bean Validation failures ────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        log.warn("Validation failed: {}", fieldErrors);
        return build(HttpStatus.BAD_REQUEST, "Bad Request",
                "Request validation failed. See 'fieldErrors' for details.", request, fieldErrors);
    }

    // ── 400 Bad Request — Malformed JSON / unreadable body ───────────────────
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.warn("Unreadable HTTP message: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Bad Request",
                "Malformed JSON or unrecognised enum value in request body.", request, null);
    }

    // ── 400 Bad Request — Path variable / query param type mismatch ──────────
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String msg = String.format("Invalid value '%s' for parameter '%s'.", ex.getValue(), ex.getName());
        log.warn("Type mismatch: {}", msg);
        return build(HttpStatus.BAD_REQUEST, "Bad Request", msg, request, null);
    }

    // ── 500 Internal Server Error — catch-all ────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please contact support.", request, null);
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors) {

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
