package com.school.student.dto;

import com.school.student.domain.Student.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Single payload that bundles all the enrollment wizard steps.
 * - personal info (always required)
 * - academic placement (classroom assignment)
 * - at least one guardian
 * - optional initial documents
 */
public record EnrollmentRequest(
        // ---- Personal ----
        String photoUrl,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Gender gender,
        @NotNull @Past LocalDate dateOfBirth,
        String placeOfBirth,
        String nationality,
        String address,
        // Geo selection from the reference data (config-service). countryCode + cityId are
        // required at enrollment; regionId is required only when the country uses regions
        // (e.g. Cameroon). Validation + denormalization happens server-side.
        String countryCode,
        UUID regionId,
        UUID cityId,
        String phone,
        String email,

        // ---- Academic ----
        UUID classroomId,
        UUID academicYearId,

        // ---- Guardians ----
        @NotEmpty @Valid List<GuardianDto> guardians,

        // ---- Documents (optional at enrollment time) ----
        @Valid List<StudentDocumentDto> documents,

        String notes
) {}
