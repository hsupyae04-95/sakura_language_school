package com.sakura.languageschool.service;

import java.util.List;

import com.sakura.languageschool.model.dto.ClassBatchRequest;
import com.sakura.languageschool.model.dto.ClassBatchResponse;
import com.sakura.languageschool.model.enums.BatchStatus;

public interface ClassBatchService {
	List<ClassBatchResponse> getAllBatches();

    List<ClassBatchResponse> getBatchesByCourse(Long courseId);

    ClassBatchResponse getBatchById(Long id);

    ClassBatchResponse createBatch(ClassBatchRequest request);

    ClassBatchResponse updateBatch(
            Long id, ClassBatchRequest request);

    void assignTeacher(Long batchId, Long teacherId);

    void updateBatchStatus(Long id, BatchStatus status);

    List<ClassBatchResponse> getTeacherAssignedBatches(String username);

    List<com.sakura.languageschool.model.dto.EnrollmentResponse> getApprovedStudents(
            Long batchId, String teacherUsername);
}
