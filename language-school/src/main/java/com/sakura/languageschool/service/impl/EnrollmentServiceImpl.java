
package com.sakura.languageschool.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakura.languageschool.bean.Mapper;
import com.sakura.languageschool.model.*;
import com.sakura.languageschool.model.dto.*;
import com.sakura.languageschool.model.enums.*;
import com.sakura.languageschool.repository.*;
import com.sakura.languageschool.service.EnrollmentService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentDao enrollmentDao;
    private final StudentDao studentDao;
    private final ClassBatchDao classBatchDao;
    private final UserDao userDao;
    private final Mapper mapper;

    @Override
    @Transactional
    public EnrollmentResponse applyForBatch(EnrollmentRequest request) {

        if (!Boolean.TRUE.equals(request.getConfirmed())) {
            throw new IllegalArgumentException(
                "You must confirm the application before submitting");
        }

        StudentProfile student = studentDao
            .findByIdAndUser_EnabledTrue(request.getStudentId())
            .orElseThrow(() ->
                new EntityNotFoundException("Active student not found"));

        ClassBatch batch = classBatchDao.findById(request.getBatchId())
            .orElseThrow(() ->
                new EntityNotFoundException("Batch not found"));

        if (batch.getStatus() != BatchStatus.OPEN) {
            throw new IllegalStateException(
                "You can only apply for an open batch");
        }

        if (batch.getStartDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalStateException(
                "Cannot apply for a batch that has already started");
        }

        Enrollment enrollment = enrollmentDao
            .findByStudent_IdAndBatch_Id(
                student.getId(), batch.getId())
            .orElse(null);

        if (enrollment != null) {

            if (enrollment.getStatus() == EnrollmentStatus.PENDING) {
                throw new IllegalStateException(
                    "You already have a pending application for this batch");
            }

            if (enrollment.getStatus() == EnrollmentStatus.APPROVED) {
                throw new IllegalStateException(
                    "You are already enrolled in this batch");
            }

            // Allow reapplication after rejection or cancellation.
            enrollment.setStatus(EnrollmentStatus.PENDING);
            enrollment.setAppliedAt(LocalDateTime.now());
            enrollment.setReviewAt(null);
            enrollment.setApprovedAt(null);
            enrollment.setReviewedBy(null);
            enrollment.setAdminNote(null);

        } else {

            enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setBatch(batch);
            enrollment.setStatus(EnrollmentStatus.PENDING);
            enrollment.setAppliedAt(LocalDateTime.now());
        }

        Enrollment saved = enrollmentDao.save(enrollment);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EnrollmentResponse applyForBatch(
            EnrollmentRequest request, String username) {
        StudentProfile student = studentDao.findByUser_Username(username)
                .filter(candidate -> candidate.getUser().isEnabled())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Active student profile not found"));

        request.setStudentId(student.getId());
        return applyForBatch(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getAllEnrollments() {

        return enrollmentDao.findAll()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getPendingEnrollments() {

        return enrollmentDao.findByStatus(EnrollmentStatus.PENDING)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getStudentEnrollments(Long studentId) {

        if (!studentDao.existsById(studentId)) {
            throw new EntityNotFoundException("Student not found");
        }

        return enrollmentDao.findByStudent_Id(studentId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String username) {
        StudentProfile student = studentDao.findByUser_Username(username)
                .filter(candidate -> candidate.getUser().isEnabled())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Active student profile not found"));

        return enrollmentDao.findByStudent_Id(student.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long id) {

        Enrollment enrollment = findEnrollment(id);

        return mapToResponse(enrollment);
    }

    @Override
    @Transactional
    public EnrollmentResponse reviewEnrollment(
        Long id,
        ReviewEnrollmentRequest request,
        Long adminId
    ) {

        Enrollment enrollment = findEnrollment(id);

        if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
            throw new IllegalStateException(
                "Only pending applications can be reviewed");
        }

        EnrollmentStatus newStatus = request.getStatus();

        if (newStatus != EnrollmentStatus.APPROVED
                && newStatus != EnrollmentStatus.REJECTED) {
            throw new IllegalArgumentException(
                "Review status must be APPROVED or REJECTED");
        }

        Users admin = userDao.findById(adminId)
            .orElseThrow(() ->
                new EntityNotFoundException("Admin account not found"));

        if (newStatus == EnrollmentStatus.APPROVED) {

            ClassBatch batch = classBatchDao
                .findByIdForUpdate(enrollment.getBatch().getId())
                .orElseThrow(() ->
                    new EntityNotFoundException("Batch not found"));

            if (batch.getStatus() != BatchStatus.OPEN) {
                throw new IllegalStateException(
                    "This batch is no longer open for enrollment");
            }

            long approvedCount = enrollmentDao
                .countByBatch_IdAndStatus(
                    batch.getId(), EnrollmentStatus.APPROVED);

            if (approvedCount >= batch.getCapacity()) {
                throw new IllegalStateException(
                    "This batch has reached its capacity");
            }

            enrollment.setApprovedAt(LocalDateTime.now());
        } else {
            enrollment.setApprovedAt(null);
        }

        enrollment.setStatus(newStatus);
        enrollment.setReviewAt(LocalDateTime.now());
        enrollment.setReviewedBy(admin);
        enrollment.setAdminNote(request.getAdminNote());

        Enrollment saved = enrollmentDao.save(enrollment);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EnrollmentResponse reviewEnrollment(
            Long id, ReviewEnrollmentRequest request, String adminUsername) {
        Users admin = userDao.findByUsername(adminUsername)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Admin account not found"));
        return reviewEnrollment(id, request, admin.getId());
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long id, Long studentId) {

        Enrollment enrollment = findEnrollment(id);

        if (!enrollment.getStudent().getId().equals(studentId)) {
            throw new IllegalArgumentException(
                "You cannot cancel another student's application");
        }

        if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
            throw new IllegalStateException(
                "Only pending applications can be cancelled");
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
    }

    private Enrollment findEnrollment(Long id) {

        return enrollmentDao.findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException("Enrollment not found"));
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {

        EnrollmentResponse response =
            mapper.map(enrollment, EnrollmentResponse.class);

        response.setStudentId(enrollment.getStudent().getId());
        response.setStudentName(
            enrollment.getStudent().getFullName());

        response.setBatchId(enrollment.getBatch().getId());
        response.setBatchName(
            enrollment.getBatch().getBatchName());

        response.setCourseId(
            enrollment.getBatch().getCourse().getId());

        response.setCourseName(
            enrollment.getBatch().getCourse().getName());

        if (enrollment.getReviewedBy() != null) {
            response.setReviewedBy(
                enrollment.getReviewedBy().getId());
        }

        return response;
    }
}
