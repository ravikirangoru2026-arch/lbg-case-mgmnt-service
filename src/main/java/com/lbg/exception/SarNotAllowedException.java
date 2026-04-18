package com.lbg.exception;

import com.lbg.enums.CaseStatus;

/**
 * Thrown when a SAR filing is attempted from a status other than PENDING_REVIEW — maps to HTTP 422.
 */
public class SarNotAllowedException extends RuntimeException {

    public SarNotAllowedException(CaseStatus currentStatus) {
        super(String.format(
            "SAR decision can only be filed when case status is PENDING_REVIEW. Current status: %s",
            currentStatus
        ));
    }
}
