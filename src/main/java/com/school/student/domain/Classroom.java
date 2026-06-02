package com.school.student.domain;

import com.school.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * A concrete class group for a given academic year: e.g. "6e A 2025-2026".
 * References (level, track, year) are stored by UUID and resolved cross-service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "classrooms", uniqueConstraints = @UniqueConstraint(name = "uk_classrooms_code_year",
        columnNames = {"code", "academic_year_id"}))
public class Classroom extends BaseEntity {

    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "academic_year_id", nullable = false)
    private UUID academicYearId;

    @Column(name = "level_id", nullable = false)
    private UUID levelId;

    @Column(name = "track_id")
    private UUID trackId;

    @Column(name = "main_teacher_id")
    private UUID mainTeacherId;

    @Column(name = "capacity")
    private Integer capacity;
}
