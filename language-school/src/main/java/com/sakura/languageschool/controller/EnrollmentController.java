package com.sakura.languageschool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.EnrollmentRequest;
import com.sakura.languageschool.service.EnrollmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final ApiUtil apiUtil;

    public EnrollmentController(EnrollmentService enrollmentService, ApiUtil apiUtil) {
        this.enrollmentService = enrollmentService;
        this.apiUtil = apiUtil;
    }

    @PostMapping
    public ResponseEntity<?> apply(
            @Valid @RequestBody EnrollmentRequest request,
            Authentication authentication) {
        return apiUtil.buildSuccessResponse(
                HttpStatus.CREATED,
                SuccessCode.SUCCESS.toString(),
                "Enrollment application submitted successfully",
                enrollmentService.applyForBatch(request, authentication.getName()));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyEnrollments(Authentication authentication) {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Student enrollments retrieved successfully",
                enrollmentService.getMyEnrollments(authentication.getName()));
    }
}
