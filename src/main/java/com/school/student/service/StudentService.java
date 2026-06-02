package com.school.student.service;

import com.school.common.audit.AuditPublisher;
import com.school.common.dto.PageResponse;
import com.school.common.exception.BusinessException;
import com.school.common.exception.ResourceNotFoundException;
import com.school.common.query.FieldDef;
import com.school.common.query.FilterGroup;
import com.school.common.query.PageInput;
import com.school.common.query.QueryRequests;
import com.school.common.query.QuerySpecifications;
import com.school.common.query.SortInput;
import com.school.student.domain.*;
import com.school.student.dto.*;
import com.school.student.mapper.StudentMapper;
import com.school.student.query.StudentFieldCatalog;
import com.school.student.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private static final String ENTITY = "Student";

    private final StudentRepository studentRepo;
    private final GuardianRepository guardianRepo;
    private final ClassroomRepository classroomRepo;
    private final StudentDocumentRepository docRepo;
    private final MatriculeGenerator matriculeGen;
    private final StudentEventPublisher events;
    private final ConfigGeoClient configGeo;
    private final StudentMapper mapper;
    private final AuditPublisher audit;

    // ---------------- Search & read ----------------

    @Transactional(readOnly = true)
    public Page<StudentDto> search(String q, UUID classroomId, Student.Status status, Pageable pageable) {
        return studentRepo.search(q, classroomId, status, pageable).map(mapper::toDto);
    }

    /**
     * Dynamic query-builder entry point (GraphQL read-side): arbitrary AND/OR filters,
     * multi-column sort and pagination, all validated against {@link StudentFieldCatalog}.
     */
    @Transactional(readOnly = true)
    public PageResponse<StudentDto> query(FilterGroup filter, List<SortInput> sort, PageInput page) {
        Specification<Student> spec = QuerySpecifications.build(filter, StudentFieldCatalog.CATALOG);
        Pageable pageable = QueryRequests.pageable(page, sort, StudentFieldCatalog.CATALOG,
                Sort.by(Sort.Direction.ASC, "lastName"));
        return PageResponse.from(studentRepo.findAll(spec, pageable), mapper::toSummary);
    }

    /** Field metadata for the client's column selector and filter builder. */
    public List<FieldDef> fields() {
        return StudentFieldCatalog.CATALOG.fields();
    }

    @Transactional(readOnly = true)
    public StudentDto get(UUID id) {
        return mapper.toDto(studentRepo.findWithGraphById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id)));
    }

    // ---------------- Enrollment workflow ----------------

    /**
     * Single-shot enrollment: creates the student, assigns matricule, attaches guardians
     * (reusing existing ones if phone/email matches) and uploads initial documents.
     */
    @Transactional
    public StudentDto enroll(EnrollmentRequest req) {
        if (req.guardians() == null || req.guardians().isEmpty()) {
            throw new BusinessException("GUARDIAN_REQUIRED", "At least one guardian is required");
        }

        Classroom classroom = null;
        String yearCode = null;
        if (req.classroomId() != null) {
            classroom = classroomRepo.findById(req.classroomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Classroom", req.classroomId()));
            assertCapacity(classroom);
        }
        if (req.academicYearId() != null) {
            yearCode = "Y" + req.academicYearId().toString().substring(0, 4).toUpperCase();
        }

        Student student = Student.builder()
                .matricule(matriculeGen.next(yearCode))
                .photoUrl(req.photoUrl())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .gender(req.gender())
                .dateOfBirth(req.dateOfBirth())
                .placeOfBirth(req.placeOfBirth())
                .nationality(req.nationality())
                .address(req.address())
                .phone(req.phone())
                .email(req.email())
                .currentClassroomId(classroom == null ? null : classroom.getId())
                .status(classroom == null ? Student.Status.PRE_REGISTERED : Student.Status.REGISTERED)
                .notes(req.notes())
                .build();
        applyGeo(student, req.countryCode(), req.regionId(), req.cityId());
        student = studentRepo.save(student);

        // Guardians
        boolean primarySeen = false;
        for (GuardianDto g : req.guardians()) {
            Guardian guardian = resolveOrCreateGuardian(g);
            StudentGuardian sg = StudentGuardian.builder()
                    .student(student)
                    .guardian(guardian)
                    .relationship(g.relationship())
                    .primary(g.primary() && !primarySeen)
                    .emergencyContact(g.emergencyContact())
                    .build();
            student.getGuardians().add(sg);
            if (g.primary()) primarySeen = true;
        }
        // Ensure at least one primary
        if (!primarySeen && !student.getGuardians().isEmpty()) {
            student.getGuardians().iterator().next().setPrimary(true);
        }

        // Documents
        if (req.documents() != null) {
            for (StudentDocumentDto d : req.documents()) {
                StudentDocument doc = StudentDocument.builder()
                        .student(student)
                        .type(d.type())
                        .label(d.label())
                        .fileUrl(d.fileUrl())
                        .contentType(d.contentType())
                        .sizeBytes(d.sizeBytes())
                        .verified(false)
                        .build();
                student.getDocuments().add(doc);
            }
        }

        events.publishEnrolled(student);
        log.info("Enrolled student {} ({} {})", student.getMatricule(),
                student.getFirstName(), student.getLastName());

        StudentDto enrolled = mapper.toDto(student);
        audit.record("STUDENT_ENROLLED", ENTITY, student.getId().toString(), null, enrolled);
        return enrolled;
    }

    @Transactional
    public StudentDto update(UUID id, UpdateStudentRequest req) {
        Student s = studentRepo.findWithGraphById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        StudentDto before = mapper.toDto(s);
        s.setPhotoUrl(req.photoUrl());
        s.setFirstName(req.firstName());
        s.setLastName(req.lastName());
        s.setGender(req.gender());
        s.setDateOfBirth(req.dateOfBirth());
        s.setPlaceOfBirth(req.placeOfBirth());
        s.setNationality(req.nationality());
        s.setAddress(req.address());
        applyGeo(s, req.countryCode(), req.regionId(), req.cityId());
        s.setPhone(req.phone());
        s.setEmail(req.email());
        s.setNotes(req.notes());
        events.publishUpdated(s);
        StudentDto after = mapper.toDto(s);
        audit.record("STUDENT_UPDATED", ENTITY, id.toString(), before, after);
        return after;
    }

    @Transactional
    public StudentDto assignClassroom(UUID id, AssignClassroomRequest req) {
        Student s = studentRepo.findWithGraphById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        Classroom c = classroomRepo.findById(req.classroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", req.classroomId()));
        assertCapacity(c);
        UUID previousClassroomId = s.getCurrentClassroomId();
        s.setCurrentClassroomId(c.getId());
        if (s.getStatus() == Student.Status.PRE_REGISTERED) {
            s.setStatus(Student.Status.REGISTERED);
        }
        events.publishClassroomAssigned(s, c);
        StudentDto after = mapper.toDto(s);
        audit.record("STUDENT_CLASSROOM_ASSIGNED", ENTITY, id.toString(),
                java.util.Map.of("classroomId", String.valueOf(previousClassroomId)),
                java.util.Map.of("classroomId", c.getId().toString(), "classroomName", c.getName()));
        return after;
    }

    @Transactional
    public StudentDto changeStatus(UUID id, Student.Status status) {
        Student s = studentRepo.findWithGraphById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        Student.Status previous = s.getStatus();
        s.setStatus(status);
        events.publishStatusChanged(s);
        audit.record("STUDENT_STATUS_CHANGED", ENTITY, id.toString(),
                java.util.Map.of("status", String.valueOf(previous)),
                java.util.Map.of("status", String.valueOf(status)));
        return mapper.toDto(s);
    }

    @Transactional
    public StudentDocumentDto addDocument(UUID studentId, StudentDocumentDto dto) {
        Student s = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        StudentDocument doc = StudentDocument.builder()
                .student(s)
                .type(dto.type())
                .label(dto.label())
                .fileUrl(dto.fileUrl())
                .contentType(dto.contentType())
                .sizeBytes(dto.sizeBytes())
                .verified(false)
                .build();
        doc = docRepo.save(doc);
        StudentDocumentDto saved = mapper.toDto(doc);
        audit.record("STUDENT_DOCUMENT_ADDED", "StudentDocument", doc.getId().toString(), null, saved);
        return saved;
    }

    @Transactional
    public void deleteDocument(UUID studentId, UUID documentId) {
        StudentDocument doc = docRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));
        if (!doc.getStudent().getId().equals(studentId)) {
            throw new BusinessException("DOC_NOT_FOR_STUDENT", "Document does not belong to this student");
        }
        StudentDocumentDto before = mapper.toDto(doc);
        docRepo.delete(doc);
        audit.record("STUDENT_DOCUMENT_DELETED", "StudentDocument", documentId.toString(), before, null);
    }

    // ---------------- Helpers ----------------

    /**
     * Validates the geographic selection against config-service's reference data and stores
     * both the structured references and the denormalized display names on the student.
     * Country and city are mandatory; the region is mandatory only for countries that use
     * regions (Cameroon), per the enrollment rules.
     */
    private void applyGeo(Student s, String countryCode, UUID regionId, UUID cityId) {
        if (countryCode == null || countryCode.isBlank()) {
            throw new BusinessException("GEO_COUNTRY_REQUIRED", "Le pays est obligatoire");
        }
        if (cityId == null) {
            throw new BusinessException("GEO_CITY_REQUIRED", "La ville est obligatoire");
        }
        if ("CM".equalsIgnoreCase(countryCode) && regionId == null) {
            throw new BusinessException("GEO_REGION_REQUIRED",
                    "La région est obligatoire pour le Cameroun");
        }
        ConfigGeoClient.GeoResolution r = configGeo.resolve(countryCode, regionId, cityId);
        s.setCountryCode(r.countryCode());
        s.setCountryName(r.countryName());
        s.setRegionId(r.regionId());
        s.setRegionName(r.regionName());
        s.setCityId(r.cityId());
        s.setCity(r.cityName());
    }

    private void assertCapacity(Classroom c) {
        if (c.getCapacity() == null) return;
        long current = classroomRepo.countStudents(c.getId());
        if (current >= c.getCapacity()) {
            throw new BusinessException("CLASSROOM_FULL",
                    "Classroom %s has reached its capacity (%d)".formatted(c.getName(), c.getCapacity()));
        }
    }

    private Guardian resolveOrCreateGuardian(GuardianDto g) {
        if (g.id() != null) {
            return guardianRepo.findById(g.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Guardian", g.id()));
        }
        if (g.email() != null && !g.email().isBlank()) {
            var existing = guardianRepo.findByEmailIgnoreCase(g.email());
            if (existing.isPresent()) return existing.get();
        }
        if (g.phone() != null && !g.phone().isBlank()) {
            var existing = guardianRepo.findByPhone(g.phone());
            if (existing.isPresent()) return existing.get();
        }
        return guardianRepo.save(Guardian.builder()
                .firstName(g.firstName())
                .lastName(g.lastName())
                .phone(g.phone())
                .email(g.email())
                .address(g.address())
                .profession(g.profession())
                .employer(g.employer())
                .nationalId(g.nationalId())
                .build());
    }
}
