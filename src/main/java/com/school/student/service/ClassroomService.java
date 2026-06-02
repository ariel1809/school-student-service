package com.school.student.service;

import com.school.common.audit.AuditPublisher;
import com.school.common.exception.ResourceNotFoundException;
import com.school.student.domain.Classroom;
import com.school.student.dto.ClassroomDto;
import com.school.student.mapper.StudentMapper;
import com.school.student.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private static final String ENTITY = "Classroom";

    private final ClassroomRepository repo;
    private final StudentMapper mapper;
    private final AuditPublisher audit;

    @Transactional(readOnly = true)
    public List<ClassroomDto> list(UUID academicYearId) {
        var rows = academicYearId == null ? repo.findAll() : repo.findAllByAcademicYearId(academicYearId);
        return rows.stream().map(c -> mapper.toDto(c, repo.countStudents(c.getId()))).toList();
    }

    @Transactional(readOnly = true)
    public ClassroomDto get(UUID id) {
        Classroom c = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Classroom", id));
        return mapper.toDto(c, repo.countStudents(c.getId()));
    }

    @Transactional
    public ClassroomDto save(ClassroomDto dto) {
        boolean creating = dto.id() == null;
        Classroom c = creating ? new Classroom()
                : repo.findById(dto.id()).orElseThrow(() -> new ResourceNotFoundException("Classroom", dto.id()));
        ClassroomDto before = creating ? null : mapper.toDto(c, repo.countStudents(c.getId()));
        c.setCode(dto.code());
        c.setName(dto.name());
        c.setAcademicYearId(dto.academicYearId());
        c.setLevelId(dto.levelId());
        c.setTrackId(dto.trackId());
        c.setMainTeacherId(dto.mainTeacherId());
        c.setCapacity(dto.capacity());
        c = repo.save(c);
        ClassroomDto after = mapper.toDto(c, repo.countStudents(c.getId()));
        audit.record(creating ? "CLASSROOM_CREATED" : "CLASSROOM_UPDATED", ENTITY, c.getId().toString(), before, after);
        return after;
    }

    @Transactional
    public void delete(UUID id) {
        repo.findById(id).ifPresent(c -> {
            ClassroomDto before = mapper.toDto(c, repo.countStudents(c.getId()));
            repo.delete(c);
            audit.record("CLASSROOM_DELETED", ENTITY, id.toString(), before, null);
        });
    }
}
