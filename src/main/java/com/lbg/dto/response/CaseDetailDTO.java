package com.lbg.dto.response;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@Schema(description = "Full case detail — returned on GET /api/cases/{id}")
public class CaseDetailDTO {
    private String caseId;
    private String customerId;
    private String status;
    private String priority;
    private String assignedAnalyst;
    private String sarDecision;
    private String sarRationale;
    private Instant openedAt;
    private Instant resolvedAt;
    private List<String> linkedAlertIds;
    private List<NoteResponseDTO> notes;
    private List<AuditLogResponseDTO> auditLog;
}