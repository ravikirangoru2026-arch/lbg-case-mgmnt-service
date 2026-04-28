package com.lbg.statemachine;

import com.lbg.enums.CaseStatus;
import com.lbg.enums.SarDecision;
import com.lbg.exception.BusinessRuleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

// statemachine/CaseStatusStateMachine.java
@Component
@Slf4j
public class CaseStatusStateMachine {

    private static final Map<CaseStatus, Set<CaseStatus>> ALLOWED_TRANSITIONS = Map.of(
        CaseStatus.OPEN,              Set.of(CaseStatus.UNDER_INVESTIGATION),
        CaseStatus.UNDER_INVESTIGATION, Set.of(CaseStatus.PENDING_REVIEW),
        CaseStatus.PENDING_REVIEW,    Set.of(CaseStatus.SAR_FILED,
                                             CaseStatus.NO_ACTION_TAKEN,
                                             CaseStatus.CLOSED),
        CaseStatus.SAR_FILED,         Set.of(),
        CaseStatus.NO_ACTION_TAKEN,   Set.of(),
        CaseStatus.CLOSED,            Set.of()
    );

    /**
     * Validates the transition is allowed.
     * Throws {@link BusinessRuleException} with 422 if not.
     */
    public void validate(CaseStatus current, CaseStatus requested) {
        Set<CaseStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(requested)) {
            log.warn("Illegal case status transition attempted: {} → {}", current, requested);
            throw new BusinessRuleException(
                String.format("Invalid status transition from %s to %s. " +
                              "Allowed from %s: %s",
                              current, requested, current,
                              allowed.isEmpty() ? "none (terminal state)" : allowed));
        }
    }

    /**
     * Derives the target status from a SAR decision.
     */
    public CaseStatus resolveFromSarDecision(SarDecision decision) {
        return switch (decision) {
            case FILE      -> CaseStatus.SAR_FILED;
            case NO_ACTION -> CaseStatus.NO_ACTION_TAKEN;
        };
    }
}