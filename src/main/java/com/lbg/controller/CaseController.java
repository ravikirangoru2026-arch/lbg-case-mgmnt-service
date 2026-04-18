package com.lbg.controller;

import com.lbg.dto.request.AddNoteRequest;
import com.lbg.dto.request.CreateCaseRequest;
import com.lbg.dto.request.FileSarRequest;
import com.lbg.dto.response.CaseDetailResponse;
import com.lbg.dto.response.CaseSummaryResponse;
import com.lbg.dto.response.NoteResponse;
import com.lbg.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Case Management REST Controller — port 8082
 *
 * Endpoints:
 *   POST   /api/cases              — Open a new case
 *   GET    /api/cases              — List cases (filter by status, priority)
 *   GET    /api/cases/{id}         — Full case detail
 *   POST   /api/cases/{id}/notes   — Add an immutable note
 *   POST   /api/cases/{id}/sar     — File SAR decision
 */
@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    // ── POST /api/cases ───────────────────────────────────────────────────────

    /**
     * Opens a new case linked to one or more alert IDs.
     * Full Bean Validation applied via @Valid.
     * Returns 201 Created with the full case detail.
     */
    @PostMapping
    public ResponseEntity<CaseDetailResponse> openCase(
            @Valid @RequestBody CreateCaseRequest request) {

        CaseDetailResponse response = caseService.openCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/cases ────────────────────────────────────────────────────────

    /**
     * Lists all cases.
     * Optional query params (AND logic when both provided):
     *   ?status=OPEN
     *   ?priority=HIGH
     *   ?status=OPEN&priority=HIGH
     */
    @GetMapping
    public ResponseEntity<List<CaseSummaryResponse>> listCases(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {

        return ResponseEntity.ok(caseService.listCases(status, priority));
    }

    // ── GET /api/cases/{id} ───────────────────────────────────────────────────

    /**
     * Returns the full case: linked alerts, notes array, auditLog array.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CaseDetailResponse> getCase(@PathVariable String id) {
        return ResponseEntity.ok(caseService.getCase(id));
    }

    // ── POST /api/cases/{id}/notes ────────────────────────────────────────────

    /**
     * Adds an immutable note to a case.
     * Notes cannot be edited or deleted once written.
     * Returns 201 Created with the saved note.
     */
    @PostMapping("/{id}/notes")
    public ResponseEntity<NoteResponse> addNote(
            @PathVariable String id,
            @Valid @RequestBody AddNoteRequest request) {

        NoteResponse response = caseService.addNote(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── POST /api/cases/{id}/sar ──────────────────────────────────────────────

    /**
     * Files a SAR decision (FILE or NO_ACTION) with mandatory rationale.
     * Only valid when case is in PENDING_REVIEW — otherwise returns 422.
     * Returns 200 OK with updated full case detail.
     */
    @PostMapping("/{id}/sar")
    public ResponseEntity<CaseDetailResponse> fileSar(
            @PathVariable String id,
            @Valid @RequestBody FileSarRequest request) {

        CaseDetailResponse response = caseService.fileSar(id, request);
        return ResponseEntity.ok(response);
    }
}
