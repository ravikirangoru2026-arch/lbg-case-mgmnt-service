package com.lbg.service;

import com.lbg.dto.request.CaseFilterCriteria;
import com.lbg.dto.request.CaseRequestDTO;
import com.lbg.dto.request.NoteRequestDTO;
import com.lbg.dto.request.SarRequestDTO;
import com.lbg.dto.response.CaseDetailDTO;
import com.lbg.dto.response.CaseSummaryDTO;
import com.lbg.dto.response.NoteResponseDTO;
import com.lbg.dto.response.PagedResponseDTO;
import com.lbg.entity.CaseAuditLog;
import com.lbg.entity.CaseNote;
import com.lbg.entity.InvestigationCase;
import com.lbg.enums.AuditEventType;
import com.lbg.enums.CaseStatus;
import com.lbg.enums.SarDecision;
import com.lbg.exception.BusinessRuleException;
import com.lbg.exception.ResourceNotFoundException;
import com.lbg.mapper.CaseMapper;
import com.lbg.repository.CaseRepository;
import com.lbg.repository.CaseSpecification;
import com.lbg.statemachine.CaseStatusStateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepository;
    private final CaseMapper caseMapper;
    private final CaseStatusStateMachine stateMachine;

    // ── POST /api/cases ──────────────────────────────────────
    @Override
    @Transactional
    public CaseDetailDTO openCase(CaseRequestDTO dto) {
        String caseRef = generateCaseRef();
        log.info("Opening case ref={} for customerId={} alerts={}",
                caseRef, dto.getCustomerId(), dto.getLinkedAlertIds());

        InvestigationCase entity = caseMapper.toEntity(dto, caseRef);
        InvestigationCase saved = caseRepository.save(entity);

        log.info("Case created: ref={} id={}", saved.getCaseRef(), saved.getId());
        return caseMapper.toDetailDTO(saved);
    }

    // ── GET /api/cases ───────────────────────────────────────
    @Override
    public PagedResponseDTO<CaseSummaryDTO> getCases(
            CaseFilterCriteria filter, int page, int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by("openedAt").descending());

        Specification<InvestigationCase> spec =
                CaseSpecification.withFilters(filter.getStatus(), filter.getPriority());

        Page<CaseSummaryDTO> result = caseRepository.findAll(spec, pageable)
                .map(caseMapper::toSummaryDTO);

        log.debug("Fetched {} cases (status={} priority={} page={} size={})",
                result.getTotalElements(), filter.getStatus(), filter.getPriority(), page, size);
        return PagedResponseDTO.from(result);
    }

    // ── GET /api/cases/{id} ──────────────────────────────────
    @Override
    public CaseDetailDTO getCaseById(String caseRef) {
        log.debug("Fetching case detail ref={}", caseRef);
        return caseMapper.toDetailDTO(findOrThrow(caseRef));
    }

    // ── POST /api/cases/{id}/notes ───────────────────────────
    @Override
    @Transactional
    public NoteResponseDTO addNote(String caseRef, NoteRequestDTO dto) {
        InvestigationCase c = findOrThrow(caseRef);
        log.info("Adding note to case ref={} author={}", caseRef, dto.getAuthor());

        // Append immutable note
        CaseNote note = CaseNote.builder()
                .investigationCase(c)
                .author(dto.getAuthor())
                .noteText(dto.getText())
                .build();
        c.getNotes().add(note);

        // Automatic audit entry
        c.getAuditLog().add(CaseAuditLog.builder()
                .investigationCase(c)
                .eventType(AuditEventType.NOTE_ADDED)
                .analyst(dto.getAuthor())
                .detail("Investigation note added by " + dto.getAuthor())
                .build());

        InvestigationCase saved = caseRepository.save(c);
        CaseNote savedNote = saved.getNotes().get(saved.getNotes().size() - 1);
        log.info("Note added: noteId={} caseRef={}", savedNote.getId(), caseRef);

        return NoteResponseDTO.builder()
                .id(savedNote.getId())
                .author(savedNote.getAuthor())
                .text(savedNote.getNoteText())
                .createdAt(savedNote.getCreatedAt())
                .build();
    }

    // ── POST /api/cases/{id}/sar ─────────────────────────────
    @Override
    @Transactional
    public CaseDetailDTO fileSar(String caseRef, SarRequestDTO dto) {
        InvestigationCase c = findOrThrow(caseRef);
        log.info("SAR filing requested: ref={} decision={} currentStatus={}",
                caseRef, dto.getDecision(), c.getStatus());

        // Guard: SAR only valid from PENDING_REVIEW
        if (c.getStatus() != CaseStatus.PENDING_REVIEW) {
            throw new BusinessRuleException(
                    "SAR can only be filed from PENDING_REVIEW status. " +
                            "Current status: " + c.getStatus());
        }

        SarDecision decision = SarDecision.valueOf(dto.getDecision());
        CaseStatus newStatus = stateMachine.resolveFromSarDecision(decision);

        // Validate the resolved transition (SAR_FILED / NO_ACTION_TAKEN)
        stateMachine.validate(c.getStatus(), newStatus);

        // Apply SAR decision
        c.setSarDecision(decision);
        c.setSarRationale(dto.getRationale());
        c.setStatus(newStatus);
        c.setResolvedAt(Instant.now());

        // Automatic audit — distinguish FILE vs NO_ACTION event type
        AuditEventType auditEvent = decision == SarDecision.FILE
                ? AuditEventType.SAR_FILED
                : AuditEventType.SAR_DECISION;

        c.getAuditLog().add(CaseAuditLog.builder()
                .investigationCase(c)
                .eventType(auditEvent)
                .analyst(c.getAssignedAnalyst())
                .detail(String.format("SAR decision: %s. Case moved to %s.", decision, newStatus))
                .build());

        InvestigationCase saved = caseRepository.save(c);
        log.info("SAR filed: ref={} decision={} newStatus={}", caseRef, decision, newStatus);
        return caseMapper.toDetailDTO(saved);
    }

    // ── Helpers ──────────────────────────────────────────────
    private InvestigationCase findOrThrow(String caseRef) {
        return caseRepository.findByCaseRef(caseRef)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Case not found with ref: " + caseRef));
    }

    private String generateCaseRef() {
        return String.format("CASE-%d-%04d",
                LocalDate.now().getYear(),
                (int) (System.currentTimeMillis() % 9999) + 1);
    }
}