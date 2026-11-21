package com.sakura.languageschool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakura.languageschool.model.Enrollment;
import com.sakura.languageschool.model.enums.EnrollmentStatus;

public interface EnrollmentDao extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudent_IdAndBatch_Id(
        Long studentId, Long batchId
    );

    List<Enrollment> findByStudent_Id(Long studentId);

    List<Enrollment> findByBatch_Id(Long batchId);

    List<Enrollment> findByBatch_IdAndStatus(
        Long batchId, EnrollmentStatus status
    );

    List<Enrollment> findByStatus(EnrollmentStatus status);

    long countByBatch_IdAndStatus(
        Long batchId, EnrollmentStatus status
    );
}
