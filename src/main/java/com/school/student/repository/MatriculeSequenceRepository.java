package com.school.student.repository;

import com.school.student.domain.MatriculeSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface MatriculeSequenceRepository extends JpaRepository<MatriculeSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<MatriculeSequence> findByYearCode(String yearCode);
}
