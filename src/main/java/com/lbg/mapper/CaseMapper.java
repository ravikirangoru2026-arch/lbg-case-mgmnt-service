package com.lbg.mapper;

import com.lbg.dto.response.AuditLogResponse;
import com.lbg.dto.response.CaseDetailResponse;
import com.lbg.dto.response.CaseSummaryResponse;
import com.lbg.dto.response.NoteResponse;
import com.lbg.entity.CaseAuditLogEntity;
import com.lbg.entity.CaseEntity;
import com.lbg.entity.CaseLinkedAlertEntity;
import com.lbg.entity.CaseNoteEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CaseMapper {

    public CaseSummaryResponse toSummary(CaseEntity entity) {
        return CaseSummaryResponse.builder()
                .caseId(entity.getCaseId())
                .customerId(entity.getCustomerId())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .assignedAnalyst(entity.getAssignedAnalyst())
                .openedAt(entity.getOpenedAt())
                .sarDecision(entity.getSarDecision())
                .linkedAlertCount(entity.getLinkedAlerts().size())
                .build();
    }

    public CaseDetailResponse toDetail(CaseEntity entity) {
        List<String> alertIds = entity.getLinkedAlerts().stream()
                .map(CaseLinkedAlertEntity::getAlertId)
                .toList();

        List<NoteResponse> notes = entity.getNotes().stream()
                .map(this::toNoteResponse)
                .toList();

        List<AuditLogResponse> auditLog = entity.getAuditLog().stream()
                .map(this::toAuditResponse)
                .toList();

        return CaseDetailResponse.builder()
                .caseId(entity.getCaseId())
                .customerId(entity.getCustomerId())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .assignedAnalyst(entity.getAssignedAnalyst())
                .openedAt(entity.getOpenedAt())
                .sarDecision(entity.getSarDecision())
                .sarRationale(entity.getSarRationale())
                .linkedAlertIds(alertIds)
                .notes(notes)
                .auditLog(auditLog)
                .build();
    }

    public NoteResponse toNoteResponse(CaseNoteEntity entity) {
        return NoteResponse.builder()
                .id(entity.getId())
                .author(entity.getAuthor())
                .noteTimestamp(entity.getNoteTimestamp())
                .noteText(entity.getNoteText())
                .build();
    }

    public AuditLogResponse toAuditResponse(CaseAuditLogEntity entity) {
        return AuditLogResponse.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .eventTimestamp(entity.getEventTimestamp())
                .analyst(entity.getAnalyst())
                .detail(entity.getDetail())
                .build();
    }
}
