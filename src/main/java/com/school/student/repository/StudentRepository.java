package com.school.student.repository;

import com.school.student.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {

    @EntityGraph(attributePaths = {"guardians", "guardians.guardian", "documents"})
    Optional<Student> findWithGraphById(UUID id);

    Optional<Student> findByMatricule(String matricule);

    @Query("""
        select s from Student s
        where (:q is null or :q = ''
               or lower(s.firstName) like lower(concat('%', :q, '%'))
               or lower(s.lastName) like lower(concat('%', :q, '%'))
               or lower(s.matricule) like lower(concat('%', :q, '%')))
          and (:classroomId is null or s.currentClassroomId = :classroomId)
          and (:status is null or s.status = :status)
        """)
    Page<Student> search(@Param("q") String q,
                         @Param("classroomId") UUID classroomId,
                         @Param("status") Student.Status status,
                         Pageable pageable);
}
