package com.school.student.repository;

import com.school.student.domain.StudentDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudentDocumentRepository extends JpaRepository<StudentDocument, UUID> {
    List<StudentDocument> findAllByStudentId(UUID studentId);
}
