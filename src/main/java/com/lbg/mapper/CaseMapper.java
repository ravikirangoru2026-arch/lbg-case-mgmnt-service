package com.lbg.mapper;

import com.lbg.dto.request.CaseRequestDTO;
import com.lbg.dto.response.AuditLogResponseDTO;
import com.lbg.dto.response.CaseDetailDTO;
import com.lbg.dto.response.CaseSummaryDTO;
import com.lbg.dto.response.NoteResponseDTO;
import com.lbg.entity.CaseAuditLog;
import com.lbg.entity.CaseLinkedAlert;
import com.lbg.entity.InvestigationCase;
import com.lbg.enums.AuditEventType;
import com.lbg.enums.CasePriority;
import com.lbg.enums.CaseStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class CaseMapper {

    public CaseSummaryDTO toSummaryDTO(InvestigationCase c) {
        return CaseSummaryDTO.builder()
                .caseId(c.getCaseRef())
                .customerId(c.getCustomerId())
                .status(c.getStatus().name())
                .priority(c.getPriority().name())
                .assignedAnalyst(c.getAssignedAnalyst())
                .openedAt(c.getOpenedAt())
                .alertCount(c.getLinkedAlerts().size())
                .build();
    }

    public CaseDetailDTO toDetailDTO(InvestigationCase c) {
        List<String> alertRefs = c.getLinkedAlerts().stream()
                .map(CaseLinkedAlert::getAlertRef).toList();

        List<NoteResponseDTO> notes = c.getNotes().stream()
                .map(n -> NoteResponseDTO.builder()
                        .id(n.getId()).author(n.getAuthor())
                        .text(n.getNoteText()).createdAt(n.getCreatedAt())
                        .build())
                .toList();

        List<AuditLogResponseDTO> auditLog = c.getAuditLog().stream()
                .map(a -> AuditLogResponseDTO.builder()
                        .eventType(a.getEventType().name())
                        .analyst(a.getAnalyst())
                        .detail(a.getDetail())
                        .changedAt(a.getChangedAt())
                        .build())
                .toList();

        return CaseDetailDTO.builder()
                .caseId(c.getCaseRef())
                .customerId(c.getCustomerId())
                .status(c.getStatus().name())
                .priority(c.getPriority().name())
                .assignedAnalyst(c.getAssignedAnalyst())
                .sarDecision(c.getSarDecision() != null ? c.getSarDecision().name() : null)
                .sarRationale(c.getSarRationale())
                .openedAt(c.getOpenedAt())
                .resolvedAt(c.getResolvedAt())
                .linkedAlertIds(alertRefs)
                .notes(notes)
                .auditLog(auditLog)
                .build();
    }

    public InvestigationCase toEntity(CaseRequestDTO dto, String caseRef) {
        InvestigationCase c = InvestigationCase.builder()
                .caseRef(caseRef)
                .customerId(dto.getCustomerId())
                .priority(CasePriority.valueOf(dto.getPriority()))
                .status(CaseStatus.OPEN)
                .assignedAnalyst(dto.getAssignedAnalyst())
                .openedAt(Instant.now())
                .build();

        // Link alerts
        dto.getLinkedAlertIds().stream()
                .map(ref -> CaseLinkedAlert.builder()
                        .investigationCase(c).alertRef(ref).build())
                .forEach(c.getLinkedAlerts()::add);

        // Opening audit entry
        c.getAuditLog().add(CaseAuditLog.builder()
                .investigationCase(c)
                .eventType(AuditEventType.CASE_OPENED)
                .analyst(dto.getAssignedAnalyst())
                .detail("Case opened and linked to alert(s): " +
                        String.join(", ", dto.getLinkedAlertIds()))
                .build());

        return c;
    }
}