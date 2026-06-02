package com.school.student.web;

import com.school.common.dto.ApiResponse;
import com.school.student.dto.ClassroomDto;
import com.school.student.service.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private static final String READ =
            "hasAuthority('CLASSROOM_READ') or hasAuthority('STUDENT_READ') or hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('SECRETARY') or hasRole('PEDAGOGIC_MANAGER') or hasRole('TEACHER')";
    private static final String WRITE =
            "hasAuthority('CLASSROOM_MANAGE') or hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('PEDAGOGIC_MANAGER')";

    private final ClassroomService service;

    @GetMapping
    @PreAuthorize(READ)
    public ApiResponse<List<ClassroomDto>> list(@RequestParam(required = false) UUID academicYearId) {
        return ApiResponse.ok(service.list(academicYearId));
    }

    @GetMapping("/{id}")
    @PreAuthorize(READ)
    public ApiResponse<ClassroomDto> get(@PathVariable UUID id) {
        return ApiResponse.ok(service.get(id));
    }

    @PostMapping
    @PreAuthorize(WRITE)
    public ApiResponse<ClassroomDto> create(@Valid @RequestBody ClassroomDto dto) {
        return ApiResponse.ok(service.save(dto), "Classroom created");
    }

    @PutMapping("/{id}")
    @PreAuthorize(WRITE)
    public ApiResponse<ClassroomDto> update(@PathVariable UUID id, @Valid @RequestBody ClassroomDto dto) {
        ClassroomDto with = ClassroomDto.builder()
                .id(id).code(dto.code()).name(dto.name())
                .academicYearId(dto.academicYearId()).levelId(dto.levelId())
                .trackId(dto.trackId()).mainTeacherId(dto.mainTeacherId())
                .capacity(dto.capacity())
                .build();
        return ApiResponse.ok(service.save(with));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(WRITE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
