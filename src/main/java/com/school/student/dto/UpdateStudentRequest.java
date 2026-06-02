package com.school.student.dto;

import com.school.student.domain.Student.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateStudentRequest(
        String photoUrl,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Gender gender,
        @NotNull @Past LocalDate dateOfBirth,
        String placeOfBirth,
        String nationality,
        String address,
        // Geo selection from the reference data (config-service).
        String countryCode,
        UUID regionId,
        UUID cityId,
        String phone,
        String email,
        String notes
) {}
