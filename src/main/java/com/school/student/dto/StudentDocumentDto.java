package com.school.student.dto;

import com.school.student.domain.StudentDocument.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record StudentDocumentDto(
        UUID id,
        @NotNull DocumentType type,
        String label,
        @NotBlank String fileUrl,
        String contentType,
        Long sizeBytes,
        boolean verified
) {}
