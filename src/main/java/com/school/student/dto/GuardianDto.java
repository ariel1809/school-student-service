package com.school.student.dto;

import com.school.student.domain.StudentGuardian.Relationship;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GuardianDto(
        UUID id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phone,
        @Email String email,
        String address,
        String profession,
        String employer,
        String nationalId,
        @NotNull Relationship relationship,
        boolean primary,
        boolean emergencyContact
) {}
