package com.lbg.entity;

import com.lbg.enums.CasePriority;
import com.lbg.enums.CaseStatus;
import com.lbg.enums.SarDecision;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseEntity {

    @Id
    @Column(name = "case_id", length = 20)
    private String caseId;

    @Column(name = "customer_id", nullable = false, length = 20)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    private CasePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CaseStatus status;

    @Column(name = "assigned_analyst", nullable = false, length = 50)
    private String assignedAnalyst;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "sar_decision", length = 20)
    private SarDecision sarDecision;

    @Lob
    @Column(name = "sar_rationale")
    private String sarRationale;

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CaseLinkedAlertEntity> linkedAlerts = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("noteTimestamp ASC")
    @Builder.Default
    private List<CaseNoteEntity> notes = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("eventTimestamp ASC")
    @Builder.Default
    private List<CaseAuditLogEntity> auditLog = new ArrayList<>();
}
