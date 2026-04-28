package com.lbg.repository;

import com.lbg.entity.InvestigationCase;
import com.lbg.enums.CasePriority;
import com.lbg.enums.CaseStatus;
import org.springframework.data.jpa.domain.Specification;

public class CaseSpecification {

    private CaseSpecification() {}

    public static Specification<InvestigationCase> withFilters(
            String status, String priority) {

        return Specification
                .where(equalIfPresent("status",   toEnum(status,   CaseStatus.class)))
                .and(equalIfPresent("priority",   toEnum(priority, CasePriority.class)));
    }

    private static <T> Specification<InvestigationCase> equalIfPresent(
            String field, T value) {
        return (root, query, cb) ->
                value == null ? null : cb.equal(root.get(field), value);
    }

    private static <E extends Enum<E>> E toEnum(String value, Class<E> type) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(type, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid filter value '" + value + "' for " + type.getSimpleName());
        }
    }
}