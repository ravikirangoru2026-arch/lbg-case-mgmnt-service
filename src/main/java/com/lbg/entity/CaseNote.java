package com.lbg.entity;

import com.lbg.enums.*;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.Instant;

@Entity
@Table(name = "case_note")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CaseNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private InvestigationCase investigationCase;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(name = "note_text", nullable = false, columnDefinition = "CLOB")
    private String noteText;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Instant createdAt;
}