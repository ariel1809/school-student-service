package com.school.student.web;

import com.school.common.dto.ApiResponse;
import com.school.common.dto.PageResponse;
import com.school.student.domain.Student;
import com.school.student.dto.*;
import com.school.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private static final String READ =
            "hasAuthority('STUDENT_READ') or hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('SECRETARY') or hasRole('PEDAGOGIC_MANAGER') or hasRole('ACCOUNTANT')";
    private static final String WRITE =
            "hasAuthority('STUDENT_MANAGE') or hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('SECRETARY')";

    private final StudentService service;

    @GetMapping
    @PreAuthorize(READ)
    public ApiResponse<PageResponse<StudentDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID classroomId,
            @RequestParam(required = false) Student.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction dir = parts.length > 1
                ? Sort.Direction.fromOptionalString(parts[1]).orElse(Sort.Direction.ASC)
                : Sort.Direction.ASC;
        var pageable = PageRequest.of(page, Math.min(size, 200), Sort.by(dir, parts[0]));
        return ApiResponse.ok(PageResponse.from(service.search(q, classroomId, status, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize(READ)
    public ApiResponse<StudentDto> get(@PathVariable UUID id) {
        return ApiResponse.ok(service.get(id));
    }

    @PostMapping("/enroll")
    @PreAuthorize(WRITE)
    public ApiResponse<StudentDto> enroll(@Valid @RequestBody EnrollmentRequest req) {
        return ApiResponse.ok(service.enroll(req), "Student enrolled");
    }

    @PutMapping("/{id}")
    @PreAuthorize(WRITE)
    public ApiResponse<StudentDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateStudentRequest req) {
        return ApiResponse.ok(service.update(id, req), "Student updated");
    }

    @PostMapping("/{id}/classroom")
    @PreAuthorize(WRITE)
    public ApiResponse<StudentDto> assignClassroom(@PathVariable UUID id, @Valid @RequestBody AssignClassroomRequest req) {
        return ApiResponse.ok(service.assignClassroom(id, req), "Classroom assigned");
    }

    @PostMapping("/{id}/status")
    @PreAuthorize(WRITE)
    public ApiResponse<StudentDto> changeStatus(@PathVariable UUID id, @RequestParam Student.Status status) {
        return ApiResponse.ok(service.changeStatus(id, status), "Status changed");
    }

    @PostMapping("/{id}/documents")
    @PreAuthorize(WRITE)
    public ApiResponse<StudentDocumentDto> addDocument(@PathVariable UUID id, @Valid @RequestBody StudentDocumentDto dto) {
        return ApiResponse.ok(service.addDocument(id, dto), "Document added");
    }

    @DeleteMapping("/{id}/documents/{docId}")
    @PreAuthorize(WRITE)
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id, @PathVariable UUID docId) {
        service.deleteDocument(id, docId);
        return ResponseEntity.noContent().build();
    }
}
