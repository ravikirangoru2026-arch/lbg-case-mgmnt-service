package com.lbg.exception;

/**
 * Thrown when a case with the generated ID already exists — maps to HTTP 409.
 */
public class DuplicateCaseException extends RuntimeException {

    public DuplicateCaseException(String caseId) {
        super("A case with ID " + caseId + " already exists.");
    }
}
