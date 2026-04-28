package com.lbg.entity;

import com.lbg.enums.*;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "investigation_case")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvestigationCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_ref", nullable = false, unique = true, length = 20)
    private String caseRef;

    @Column(name = "customer_id", nullable = false, length = 30)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CasePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private CaseStatus status;

    @Column(name = "assigned_analyst", nullable = false, length = 100)
    private String assignedAnalyst;

    @Enumerated(EnumType.STRING)
    @Column(name = "sar_decision", length = 15)
    private SarDecision sarDecision;

    @Column(name = "sar_rationale", columnDefinition = "CLOB")
    private String sarRationale;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(mappedBy = "investigationCase", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CaseLinkedAlert> linkedAlerts = new ArrayList<>();

    @OneToMany(mappedBy = "investigationCase", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<CaseNote> notes = new ArrayList<>();

    @OneToMany(mappedBy = "investigationCase", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("changedAt ASC")
    @Builder.Default
    private List<CaseAuditLog> auditLog = new ArrayList<>();
}