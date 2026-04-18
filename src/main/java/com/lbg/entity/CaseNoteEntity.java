package com.lbg.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseEntity caseEntity;

    @Column(name = "author", nullable = false, length = 50)
    private String author;

    @Column(name = "note_timestamp", nullable = false)
    private LocalDateTime noteTimestamp;

    @Lob
    @Column(name = "note_text", nullable = false)
    private String noteText;
}
