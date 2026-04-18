package com.lbg.exception;

import com.lbg.enums.CaseStatus;

/**
 * Thrown when a status transition skips a lifecycle stage — maps to HTTP 422.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(CaseStatus from, CaseStatus to) {
        super(String.format(
            "Invalid status transition: cannot move from %s to %s. " +
            "Lifecycle must follow: OPEN → UNDER_INVESTIGATION → PENDING_REVIEW → SAR_FILED / NO_ACTION_TAKEN / CLOSED",
            from, to
        ));
    }
}
