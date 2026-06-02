package com.school.student.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Atomic counter for auto-generated matricules, per academic year.
 * Pessimistic locking is used at the repository layer to ensure uniqueness
 * even under concurrent enrollments.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "matricule_sequences")
public class MatriculeSequence {

    @Id
    @Column(name = "year_code", length = 16)
    private String yearCode;

    @Column(name = "last_value", nullable = false)
    private long lastValue;

    public long nextValue() {
        return ++lastValue;
    }
}
