package com.lbg.exception;

/**
 * Thrown when a requested case does not exist — maps to HTTP 404.
 */
public class CaseNotFoundException extends RuntimeException {

    public CaseNotFoundException(String caseId) {
        super("Case not found: " + caseId);
    }
}
