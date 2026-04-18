package com.lbg.enums;

/**
 * Defines the ordered lifecycle of a case.
 * Stage skipping (non-sequential transitions) must return HTTP 422.
 *
 * Lifecycle:
 *   OPEN → UNDER_INVESTIGATION → PENDING_REVIEW → SAR_FILED | NO_ACTION_TAKEN | CLOSED
 */
public enum CaseStatus {

    OPEN(0),
    UNDER_INVESTIGATION(1),
    PENDING_REVIEW(2),
    SAR_FILED(3),
    NO_ACTION_TAKEN(3),
    CLOSED(3);

    private final int order;

    CaseStatus(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    /**
     * Returns true if transitioning from this status to {@code next} is a valid
     * sequential move (no stage skipping).
     */
    public boolean canTransitionTo(CaseStatus next) {
        return next.order == this.order + 1;
    }
}
