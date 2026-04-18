package com.lbg.config;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe sequential case ID generator.
 * Produces IDs in the format: CASE-{YEAR}-{SEQUENCE_4_DIGITS}
 * e.g. CASE-2024-0009
 */
@Component
public class CaseIdGenerator {

    private final AtomicInteger sequence = new AtomicInteger(0);

    public String next() {
        int year = Year.now().getValue();
        int seq  = sequence.incrementAndGet();
        return String.format("CASE-%d-%04d", year, seq);
    }

    /**
     * Called at startup to seed the sequence above the highest existing case number,
     * preventing ID collisions with Flyway-seeded data.
     */
    public void seedFrom(int highestExistingSequence) {
        sequence.set(highestExistingSequence);
    }
}
