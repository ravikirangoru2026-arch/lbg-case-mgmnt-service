package com.lbg.dto.response;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Getter
@Builder
@Schema(description = "Case summary — returned in list responses")
public class CaseSummaryDTO {
    private String caseId;
    private String customerId;
    private String status;
    private String priority;
    private String assignedAnalyst;
    private Instant openedAt;
    private int alertCount;          // count only — not the full array
}