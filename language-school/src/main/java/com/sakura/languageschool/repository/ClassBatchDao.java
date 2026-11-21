package com.sakura.languageschool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sakura.languageschool.model.ClassBatch;
import com.sakura.languageschool.model.enums.BatchStatus;

import jakarta.persistence.LockModeType;

public interface ClassBatchDao extends JpaRepository<ClassBatch, Long>{
	List<ClassBatch> findByStatus(BatchStatus status);

    List<ClassBatch> findByCourse_Id(Long courseId);

    List<ClassBatch> findByTeacher_User_Username(String username);

    Optional<ClassBatch> findByIdAndTeacher_User_Username(Long id, String username);

    boolean existsByCourse_IdAndBatchName(
            Long courseId, String batchName);

    boolean existsByCourse_IdAndBatchNameAndIdNot(
            Long courseId, String batchName, Long id);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM ClassBatch b WHERE b.id = :id")
    Optional<ClassBatch> findByIdForUpdate(@Param("id") Long id);
}
