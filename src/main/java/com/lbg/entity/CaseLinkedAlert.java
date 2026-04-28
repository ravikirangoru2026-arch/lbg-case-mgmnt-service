package com.lbg.entity;

import com.lbg.enums.*;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.Instant;

@Entity
@Table(name = "case_linked_alert")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CaseLinkedAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private InvestigationCase investigationCase;

    @Column(name = "alert_ref", nullable = false, length = 20)
    private String alertRef;

    @Column(name = "linked_at", updatable = false)
    @CreationTimestamp
    private Instant linkedAt;
}