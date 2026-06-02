package com.school.student.repository;

import com.school.student.domain.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GuardianRepository extends JpaRepository<Guardian, UUID> {
    Optional<Guardian> findByEmailIgnoreCase(String email);

    Optional<Guardian> findByPhone(String phone);
}
