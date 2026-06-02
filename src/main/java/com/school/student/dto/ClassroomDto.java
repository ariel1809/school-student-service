package com.school.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ClassroomDto(
        UUID id,
        @NotBlank String code,
        @NotBlank String name,
        @NotNull UUID academicYearId,
        @NotNull UUID levelId,
        UUID trackId,
        UUID mainTeacherId,
        Integer capacity,
        Long studentCount
) {}
