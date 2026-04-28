package com.lbg.entity;

import com.lbg.enums.*;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.Instant;

@Entity
@Table(name = "case_audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CaseAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private InvestigationCase investigationCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private AuditEventType eventType;

    @Column(length = 100)
    private String analyst;

    @Column(nullable = false, columnDefinition = "CLOB")
    private String detail;

    @Column(name = "changed_at", updatable = false)
    @CreationTimestamp
    private Instant changedAt;
}