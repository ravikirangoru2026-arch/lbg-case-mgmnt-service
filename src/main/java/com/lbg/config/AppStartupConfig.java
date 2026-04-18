package com.lbg.config;

import com.lbg.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * On application startup, reads the highest existing case sequence number from
 * the database and seeds the CaseIdGenerator so new IDs do not collide with
 * Flyway-seeded data (e.g. CASE-2024-0008 → next is CASE-2024-0009).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppStartupConfig implements ApplicationRunner {

    private static final Pattern CASE_ID_PATTERN = Pattern.compile("CASE-\\d{4}-(\\d+)");

    private final CaseRepository caseRepository;
    private final CaseIdGenerator caseIdGenerator;

    @Override
    public void run(ApplicationArguments args) {
        caseRepository.findAll().stream()
                .map(c -> {
                    Matcher m = CASE_ID_PATTERN.matcher(c.getCaseId());
                    return m.matches() ? Integer.parseInt(m.group(1)) : 0;
                })
                .max(Comparator.naturalOrder())
                .ifPresent(max -> {
                    caseIdGenerator.seedFrom(max);
                    log.info("CaseIdGenerator seeded to sequence {}", max);
                });
    }
}
