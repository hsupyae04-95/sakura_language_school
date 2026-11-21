package com.sakura.languageschool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.ReviewEnrollmentRequest;
import com.sakura.languageschool.model.enums.EnrollmentStatus;
import com.sakura.languageschool.service.EnrollmentService;

@RestController
@RequestMapping("/api/admin/enrollments")
public class AdminEnrollmentController {
    private final EnrollmentService enrollmentService;
    private final ApiUtil apiUtil;

    public AdminEnrollmentController(EnrollmentService enrollmentService, ApiUtil apiUtil) {
        this.enrollmentService = enrollmentService;
        this.apiUtil = apiUtil;
    }

    @GetMapping
    public ResponseEntity<?> getAllEnrollments() {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "Enrollments retrieved successfully",
                enrollmentService.getAllEnrollments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEnrollmentById(@PathVariable Long id) {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "Enrollment retrieved successfully",
                enrollmentService.getEnrollmentById(id));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestBody(required = false) ReviewEnrollmentRequest request,
            Authentication authentication) {
        return review(id, reviewRequest(request, EnrollmentStatus.APPROVED),
                authentication, "Enrollment approved successfully");
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestBody(required = false) ReviewEnrollmentRequest request,
            Authentication authentication) {
        return review(id, reviewRequest(request, EnrollmentStatus.REJECTED),
                authentication, "Enrollment rejected successfully");
    }

    private ResponseEntity<?> review(
            Long id,
            ReviewEnrollmentRequest request,
            Authentication authentication,
            String message) {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(), message,
                enrollmentService.reviewEnrollment(id, request, authentication.getName()));
    }

    private ReviewEnrollmentRequest reviewRequest(
            ReviewEnrollmentRequest request, EnrollmentStatus status) {
        ReviewEnrollmentRequest review = request == null
                ? new ReviewEnrollmentRequest()
                : request;
        review.setStatus(status);
        return review;
    }
}
