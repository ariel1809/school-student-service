package com.school.student.dto;

import com.school.student.domain.Student.Gender;
import com.school.student.domain.Student.Status;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record StudentDto(
        UUID id,
        String matricule,
        String photoUrl,
        String firstName,
        String lastName,
        Gender gender,
        LocalDate dateOfBirth,
        String placeOfBirth,
        String nationality,
        String address,
        String countryCode,
        String countryName,
        UUID regionId,
        String regionName,
        UUID cityId,
        String city,
        String phone,
        String email,
        UUID currentClassroomId,
        Status status,
        String notes,
        List<GuardianDto> guardians,
        List<StudentDocumentDto> documents,
        Instant createdAt,
        Instant updatedAt
) {}
