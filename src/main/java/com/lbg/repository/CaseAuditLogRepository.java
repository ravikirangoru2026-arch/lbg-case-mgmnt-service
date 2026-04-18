package com.lbg.repository;

import com.lbg.entity.CaseAuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseAuditLogRepository extends JpaRepository<CaseAuditLogEntity, Long> {
}
