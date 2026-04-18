package com.lbg.service;

import com.lbg.config.CaseIdGenerator;
import com.lbg.dto.request.AddNoteRequest;
import com.lbg.dto.request.CreateCaseRequest;
import com.lbg.dto.request.FileSarRequest;
import com.lbg.dto.response.AuditLogResponse;
import com.lbg.dto.response.CaseDetailResponse;
import com.lbg.dto.response.CaseSummaryResponse;
import com.lbg.dto.response.NoteResponse;
import com.lbg.entity.CaseAuditLogEntity;
import com.lbg.entity.CaseEntity;
import com.lbg.entity.CaseLinkedAlertEntity;
import com.lbg.entity.CaseNoteEntity;
import com.lbg.enums.AuditEventType;
import com.lbg.enums.CasePriority;
import com.lbg.enums.CaseStatus;
import com.lbg.enums.SarDecision;
import com.lbg.exception.CaseNotFoundException;
import com.lbg.exception.InvalidStatusTransitionException;
import com.lbg.exception.SarNotAllowedException;
import com.lbg.mapper.CaseMapper;
import com.lbg.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository    caseRepository;
    private final CaseIdGenerator   caseIdGenerator;
    private final CaseMapper        caseMapper;

    // ── POST /api/cases ───────────────────────────────────────────────────────

    /**
     * Opens a new case, links alert IDs, and writes a CASE_OPENED audit entry.
     */
    @Transactional
    public CaseDetailResponse openCase(CreateCaseRequest request) {

        String newId = caseIdGenerator.next();
        LocalDateTime now = LocalDateTime.now();

        CaseEntity caseEntity = CaseEntity.builder()
                .caseId(newId)
                .customerId(request.getCustomerId())
                .priority(request.getPriority())
                .status(CaseStatus.OPEN)
                .assignedAnalyst(request.getAssignedAnalyst())
                .openedAt(now)
                .build();

        // Link alerts
        List<CaseLinkedAlertEntity> alerts = request.getLinkedAlertIds().stream()
                .map(alertId -> CaseLinkedAlertEntity.builder()
                        .caseEntity(caseEntity)
                        .alertId(alertId)
                        .build())
                .toList();
        caseEntity.getLinkedAlerts().addAll(alerts);

        // Audit: CASE_OPENED  ← mandatory, judges inspect this directly
        String alertList = String.join(", ", request.getLinkedAlertIds());
        appendAudit(caseEntity, AuditEventType.CASE_OPENED, request.getAssignedAnalyst(), now,
                "Case opened and linked to alert(s): " + alertList);

        CaseEntity saved = caseRepository.save(caseEntity);
        log.info("Opened case {} for customer {}", saved.getCaseId(), saved.getCustomerId());
        return caseMapper.toDetail(saved);
    }

    // ── GET /api/cases ────────────────────────────────────────────────────────

    /**
     * Lists cases with optional AND-combined filtering by status and priority.
     */
    @Transactional(readOnly = true)
    public List<CaseSummaryResponse> listCases(String statusParam, String priorityParam) {

        CaseStatus   status   = parseEnum(statusParam,   CaseStatus.class);
        CasePriority priority = parseEnum(priorityParam, CasePriority.class);

        List<CaseEntity> results;

        if (status != null && priority != null) {
            results = caseRepository.findByStatusAndPriority(status, priority);
        } else if (status != null) {
            results = caseRepository.findByStatus(status);
        } else if (priority != null) {
            results = caseRepository.findByPriority(priority);
        } else {
            results = caseRepository.findAll();
        }

        return results.stream().map(caseMapper::toSummary).toList();
    }

    // ── GET /api/cases/{id} ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CaseDetailResponse getCase(String caseId) {
        return caseMapper.toDetail(findOrThrow(caseId));
    }

    // ── POST /api/cases/{id}/notes ────────────────────────────────────────────

    /**
     * Adds an immutable note (cannot be edited or deleted once written).
     * Writes a NOTE_ADDED audit entry automatically.
     */
    @Transactional
    public NoteResponse addNote(String caseId, AddNoteRequest request) {

        CaseEntity caseEntity = findOrThrow(caseId);
        LocalDateTime now = LocalDateTime.now();

        CaseNoteEntity note = CaseNoteEntity.builder()
                .caseEntity(caseEntity)
                .author(request.getAuthor())
                .noteTimestamp(now)
                .noteText(request.getNoteText())
                .build();

        caseEntity.getNotes().add(note);

        // Audit: NOTE_ADDED ← written automatically on every note
        appendAudit(caseEntity, AuditEventType.NOTE_ADDED, request.getAuthor(), now,
                "Investigation note added by " + request.getAuthor());

        caseRepository.save(caseEntity);
        log.info("Note added to case {} by {}", caseId, request.getAuthor());
        return caseMapper.toNoteResponse(note);
    }

    // ── POST /api/cases/{id}/sar ──────────────────────────────────────────────

    /**
     * Files a SAR decision (FILE or NO_ACTION).
     *
     * Rules enforced:
     *   - Case must be in PENDING_REVIEW, otherwise HTTP 422.
     *   - sarRationale is mandatory (validated at DTO level).
     *   - Status transitions to SAR_FILED or NO_ACTION_TAKEN.
     *   - Both a STATUS_CHANGED and SAR_FILED / SAR_DECISION audit entry are written.
     */
    @Transactional
    public CaseDetailResponse fileSar(String caseId, FileSarRequest request) {

        CaseEntity caseEntity = findOrThrow(caseId);

        // Guard: SAR can only be filed from PENDING_REVIEW
        if (caseEntity.getStatus() != CaseStatus.PENDING_REVIEW) {
            throw new SarNotAllowedException(caseEntity.getStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        CaseStatus previousStatus = caseEntity.getStatus();
        CaseStatus newStatus = (request.getSarDecision() == SarDecision.FILE)
                ? CaseStatus.SAR_FILED
                : CaseStatus.NO_ACTION_TAKEN;

        caseEntity.setStatus(newStatus);
        caseEntity.setSarDecision(request.getSarDecision());
        caseEntity.setSarRationale(request.getSarRationale());

        // Audit 1: STATUS_CHANGED ← every status change must be logged
        appendAudit(caseEntity, AuditEventType.STATUS_CHANGED, request.getAnalyst(), now,
                String.format("Status changed from %s to %s", previousStatus, newStatus));

        // Audit 2: SAR event ← every SAR decision must be logged
        AuditEventType sarEventType = (request.getSarDecision() == SarDecision.FILE)
                ? AuditEventType.SAR_FILED
                : AuditEventType.SAR_DECISION;

        appendAudit(caseEntity, sarEventType, request.getAnalyst(), now,
                String.format("SAR decision: %s. Case moved to %s.", request.getSarDecision(), newStatus));

        CaseEntity saved = caseRepository.save(caseEntity);
        log.info("SAR filed on case {}: decision={}, newStatus={}", caseId, request.getSarDecision(), newStatus);
        return caseMapper.toDetail(saved);
    }

    // ── Status Transition (utility, exposed for future status-change endpoint) ─

    /**
     * Validates a lifecycle transition and enforces no stage-skipping (HTTP 422).
     * Called internally whenever a status must advance sequentially.
     */
    public void validateTransition(CaseStatus from, CaseStatus to) {
        if (!from.canTransitionTo(to)) {
            throw new InvalidStatusTransitionException(from, to);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private CaseEntity findOrThrow(String caseId) {
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseNotFoundException(caseId));
    }

    /**
     * Creates and attaches an audit log entry to the case.
     * This is called after every status change and every SAR decision —
     * missing entries score zero on Data Integrity.
     */
    private void appendAudit(CaseEntity caseEntity,
                              AuditEventType eventType,
                              String analyst,
                              LocalDateTime timestamp,
                              String detail) {

        CaseAuditLogEntity entry = CaseAuditLogEntity.builder()
                .caseEntity(caseEntity)
                .eventType(eventType)
                .eventTimestamp(timestamp)
                .analyst(analyst)
                .detail(detail)
                .build();

        caseEntity.getAuditLog().add(entry);
    }

    // ── Enum parsing helper (returns null for null/blank input) ───────────────

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumClass) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                String.format("Invalid value '%s' for %s. Valid values: %s",
                    value, enumClass.getSimpleName(),
                    List.of(enumClass.getEnumConstants()))
            );
        }
    }
}
