package com.lbg.repository;

import com.lbg.entity.InvestigationCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<InvestigationCase, Long>,
        JpaSpecificationExecutor<InvestigationCase> {
    Optional<InvestigationCase> findByCaseRef(String caseRef);

    boolean existsByCaseRef(String caseRef);
}