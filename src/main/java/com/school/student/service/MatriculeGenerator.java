package com.school.student.service;

import com.school.student.domain.MatriculeSequence;
import com.school.student.repository.MatriculeSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

/**
 * Generates next matricule under a per-year pessimistic lock.
 * Format (configurable): {prefix}-{YYYY}-{padded sequence}.
 *
 * REQUIRES_NEW is intentional: we want the counter increment to commit
 * even if the enclosing enrollment transaction later fails, so that a retry
 * gets a fresh number rather than risking duplicates.
 */
@Service
@RequiredArgsConstructor
public class MatriculeGenerator {

    private final MatriculeSequenceRepository repo;

    @Value("${student.matricule.prefix:STD}")
    private String prefix;

    @Value("${student.matricule.pad-width:5}")
    private int padWidth;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String next(String yearCodeOrNull) {
        String yearCode = (yearCodeOrNull == null || yearCodeOrNull.isBlank())
                ? String.valueOf(Year.now().getValue())
                : yearCodeOrNull;

        MatriculeSequence seq = repo.findByYearCode(yearCode)
                .orElseGet(() -> repo.save(MatriculeSequence.builder()
                        .yearCode(yearCode).lastValue(0L).build()));
        long n = seq.nextValue();
        repo.save(seq);

        String pattern = "%s-%s-%0" + padWidth + "d";
        return pattern.formatted(prefix, yearCode, n);
    }
}
