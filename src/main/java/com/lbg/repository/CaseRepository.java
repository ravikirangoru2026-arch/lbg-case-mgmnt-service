package com.lbg.repository;

import com.lbg.entity.CaseEntity;
import com.lbg.enums.CasePriority;
import com.lbg.enums.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends JpaRepository<CaseEntity, String> {

    List<CaseEntity> findByStatus(CaseStatus status);

    List<CaseEntity> findByPriority(CasePriority priority);

    List<CaseEntity> findByStatusAndPriority(CaseStatus status, CasePriority priority);
}
