package com.lbg.dto.response;

import com.lbg.enums.CasePriority;
import com.lbg.enums.CaseStatus;
import com.lbg.enums.SarDecision;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CaseDetailResponse {
    private String caseId;
    private String customerId;
    private CasePriority priority;
    private CaseStatus status;
    private String assignedAnalyst;
    private LocalDateTime openedAt;
    private SarDecision sarDecision;
    private String sarRationale;
    private List<String> linkedAlertIds;
    private List<NoteResponse> notes;
    private List<AuditLogResponse> auditLog;
}
