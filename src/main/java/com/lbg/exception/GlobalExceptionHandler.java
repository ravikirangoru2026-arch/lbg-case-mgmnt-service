package com.lbg.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── 400 Bean Validation ───────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {

        List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new Violation(fe.getField(),
                        fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid"))
                .toList();

        log.warn("Validation failed [{}]: {}", req.getRequestURI(), violations);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "Validation failed", req.getRequestURI(), violations));
    }

    // ── 400 Bad query param ───────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadArg(
            IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("Bad request [{}]: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, ex.getMessage(), req.getRequestURI(), List.of()));
    }

    // ── 404 Not found ─────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("Not found [{}]: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(404, ex.getMessage(), req.getRequestURI(), List.of()));
    }

    // ── 422 Business rule ─────────────────────────────────────
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex, HttpServletRequest req) {
        log.warn("Business rule violation [{}]: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.unprocessableEntity()
                .body(ErrorResponse.of(422, ex.getMessage(), req.getRequestURI(), List.of()));
    }

    // ── 500 Catch-all ─────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception [{}]", req.getRequestURI(), ex);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(500, "An unexpected error occurred",
                        req.getRequestURI(), List.of()));
    }

    // ── Response body ─────────────────────────────────────────
    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private Instant timestamp;
        private int status;
        private String error;
        private String path;
        private List<Violation> violations;

        static ErrorResponse of(int status, String error,
                                String path, List<Violation> violations) {
            return new ErrorResponse(Instant.now(), status, error, path, violations);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Violation {
        private String field;
        private String message;
    }
}