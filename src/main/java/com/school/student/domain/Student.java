package com.school.student.domain;

import com.school.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "students", uniqueConstraints = {
        @UniqueConstraint(name = "uk_students_matricule", columnNames = "matricule")
})
public class Student extends BaseEntity {

    @Column(name = "matricule", nullable = false, length = 32)
    private String matricule;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "place_of_birth", length = 200)
    private String placeOfBirth;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "address", length = 500)
    private String address;

    // Geographic reference: structured ids + denormalized display names resolved
    // from config-service at enrollment/update time. `city` holds the city name.
    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "country_name", length = 120)
    private String countryName;

    @Column(name = "region_id")
    private UUID regionId;

    @Column(name = "region_name", length = 120)
    private String regionName;

    @Column(name = "city_id")
    private UUID cityId;

    @Column(name = "city", length = 150)
    private String city;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "current_classroom_id")
    private UUID currentClassroomId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Builder.Default
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<StudentGuardian> guardians = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<StudentDocument> documents = new HashSet<>();

    public enum Gender { MALE, FEMALE, OTHER }

    public enum Status {
        PRE_REGISTERED,  // début du workflow, infos minimales
        REGISTERED,      // dossier validé, scolarité active
        SUSPENDED,
        TRANSFERRED,
        GRADUATED,
        WITHDRAWN
    }
}
