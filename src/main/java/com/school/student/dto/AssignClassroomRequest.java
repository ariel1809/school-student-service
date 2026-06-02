package com.school.student.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignClassroomRequest(@NotNull UUID classroomId) {}
