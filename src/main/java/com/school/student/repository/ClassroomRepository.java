package com.school.student.repository;

import com.school.student.domain.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    List<Classroom> findAllByAcademicYearId(UUID academicYearId);

    @Query("select count(s) from Student s where s.currentClassroomId = :id")
    long countStudents(UUID id);
}
