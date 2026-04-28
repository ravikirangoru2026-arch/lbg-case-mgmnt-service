package com.lbg.controller;

import com.lbg.dto.request.CaseFilterCriteria;
import com.lbg.dto.request.CaseRequestDTO;
import com.lbg.dto.request.NoteRequestDTO;
import com.lbg.dto.request.SarRequestDTO;
import com.lbg.dto.response.CaseDetailDTO;
import com.lbg.dto.response.CaseSummaryDTO;
import com.lbg.dto.response.NoteResponseDTO;
import com.lbg.dto.response.PagedResponseDTO;
import com.lbg.service.CaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Cases", description = "Investigation case lifecycle management APIs")
public class CaseController {

    private final CaseService caseService;

    // ── POST /api/cases ──────────────────────────────────────
    @PostMapping
    @Operation(summary = "Open a new investigation case linked to one or more alerts")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Case opened successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed — field errors returned")
    })
    public ResponseEntity<CaseDetailDTO> openCase(
            @Valid @RequestBody CaseRequestDTO dto) {

        log.info("POST /api/cases customerId={} alerts={}",
                dto.getCustomerId(), dto.getLinkedAlertIds());
        CaseDetailDTO response = caseService.openCase(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.getCaseId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    // ── GET /api/cases ───────────────────────────────────────
    @GetMapping
    @Operation(summary = "List cases with optional filtering by status and priority")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated case list"),
        @ApiResponse(responseCode = "400", description = "Invalid filter value")
    })
    public ResponseEntity<PagedResponseDTO<CaseSummaryDTO>> getCases(
            @RequestParam(required = false)
            @Parameter(description = "Filter by status: OPEN | UNDER_INVESTIGATION | PENDING_REVIEW | SAR_FILED | NO_ACTION_TAKEN | CLOSED")
            String status,

            @RequestParam(required = false)
            @Parameter(description = "Filter by priority: HIGH | MEDIUM | LOW")
            String priority,

            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/cases status={} priority={} page={} size={}", status, priority, page, size);
        CaseFilterCriteria filter = new CaseFilterCriteria(status, priority);
        return ResponseEntity.ok(caseService.getCases(filter, page, size));
    }

    // ── GET /api/cases/{id} ──────────────────────────────────
    @GetMapping("/{caseRef}")
    @Operation(summary = "Get full case detail — includes linked alerts, notes, and audit log")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Case found"),
        @ApiResponse(responseCode = "404", description = "Case not found")
    })
    public ResponseEntity<CaseDetailDTO> getCaseById(
            @PathVariable
            @Parameter(description = "Case reference e.g. CASE-2024-0001")
            String caseRef) {

        log.info("GET /api/cases/{}", caseRef);
        return ResponseEntity.ok(caseService.getCaseById(caseRef));
    }

    // ── POST /api/cases/{id}/notes ───────────────────────────
    @PostMapping("/{caseRef}/notes")
    @Operation(summary = "Add an immutable note to a case — cannot be edited or deleted")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Note added"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Case not found")
    })
    public ResponseEntity<NoteResponseDTO> addNote(
            @PathVariable String caseRef,
            @Valid @RequestBody NoteRequestDTO dto) {

        log.info("POST /api/cases/{}/notes author={}", caseRef, dto.getAuthor());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(caseService.addNote(caseRef, dto));
    }

    // ── POST /api/cases/{id}/sar ─────────────────────────────
    @PostMapping("/{caseRef}/sar")
    @Operation(
        summary = "File a SAR decision for a case",
        description = """
            Files a Suspicious Activity Report decision (FILE or NO_ACTION).
            **Only valid when case status is PENDING_REVIEW.**
            Any other status returns 422 with a descriptive message.
            Lifecycle enforced: OPEN → UNDER_INVESTIGATION → PENDING_REVIEW → SAR_FILED / NO_ACTION_TAKEN
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "SAR decision filed, case status updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed — decision or rationale missing/invalid"),
        @ApiResponse(responseCode = "404", description = "Case not found"),
        @ApiResponse(responseCode = "422",
            description = "Business rule violation — case not in PENDING_REVIEW status")
    })
    public ResponseEntity<CaseDetailDTO> fileSar(
            @PathVariable String caseRef,
            @Valid @RequestBody SarRequestDTO dto) {

        log.info("POST /api/cases/{}/sar decision={}", caseRef, dto.getDecision());
        return ResponseEntity.ok(caseService.fileSar(caseRef, dto));
    }
}