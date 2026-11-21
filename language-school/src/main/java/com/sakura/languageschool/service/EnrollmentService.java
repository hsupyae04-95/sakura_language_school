package com.sakura.languageschool.service;

import java.util.List;

import com.sakura.languageschool.model.dto.EnrollmentRequest;
import com.sakura.languageschool.model.dto.EnrollmentResponse;
import com.sakura.languageschool.model.dto.ReviewEnrollmentRequest;

public interface EnrollmentService {

    EnrollmentResponse applyForBatch(EnrollmentRequest request);

    EnrollmentResponse applyForBatch(EnrollmentRequest request, String username);

    List<EnrollmentResponse> getAllEnrollments();

    List<EnrollmentResponse> getPendingEnrollments();

    List<EnrollmentResponse> getStudentEnrollments(Long studentId);

    List<EnrollmentResponse> getMyEnrollments(String username);

    EnrollmentResponse getEnrollmentById(Long id);

    EnrollmentResponse reviewEnrollment(
        Long id, ReviewEnrollmentRequest request, Long adminId
    );

    EnrollmentResponse reviewEnrollment(
        Long id, ReviewEnrollmentRequest request, String adminUsername
    );

    void cancelEnrollment(Long id, Long studentId);
}
