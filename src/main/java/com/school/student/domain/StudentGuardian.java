package com.school.student.domain;

import com.school.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "student_guardians",
        uniqueConstraints = @UniqueConstraint(name = "uk_student_guardian_pair",
                columnNames = {"student_id", "guardian_id", "relationship"}))
public class StudentGuardian extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guardian_id", nullable = false)
    private Guardian guardian;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", nullable = false, length = 20)
    private Relationship relationship;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "emergency_contact", nullable = false)
    private boolean emergencyContact;

    public enum Relationship {
        FATHER, MOTHER, GUARDIAN, GRANDFATHER, GRANDMOTHER, UNCLE, AUNT, SIBLING, OTHER
    }
}
