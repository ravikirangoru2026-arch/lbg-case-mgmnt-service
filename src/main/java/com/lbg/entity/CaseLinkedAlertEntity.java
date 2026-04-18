package com.lbg.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "case_linked_alerts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"case_id", "alert_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseLinkedAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseEntity caseEntity;

    @Column(name = "alert_id", nullable = false, length = 20)
    private String alertId;
}
