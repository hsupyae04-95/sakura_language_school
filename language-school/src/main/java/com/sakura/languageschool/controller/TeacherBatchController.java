package com.sakura.languageschool.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.ClassBatchResponse;
import com.sakura.languageschool.model.dto.EnrollmentResponse;
import com.sakura.languageschool.service.ClassBatchService;

@RestController
@RequestMapping("/api/teacher/batches")
public class TeacherBatchController {
	private final ClassBatchService classBatchService;
	private final ApiUtil apiUtil;

	public TeacherBatchController(ClassBatchService classBatchService, ApiUtil apiUtil) {
		this.classBatchService = classBatchService;
		this.apiUtil = apiUtil;
	}

	@GetMapping
	public ResponseEntity<?> getAssignedBatches(Authentication authentication) {
		List<ClassBatchResponse> batches = classBatchService
				.getTeacherAssignedBatches(authentication.getName());
		return apiUtil.buildSuccessResponse(
				HttpStatus.OK, SuccessCode.SUCCESS.toString(),
				"Assigned batches retrieved successfully", batches);
	}

	@GetMapping("/{id}/students")
	public ResponseEntity<?> getApprovedStudents(
			@PathVariable Long id, Authentication authentication) {
		List<EnrollmentResponse> students = classBatchService
				.getApprovedStudents(id, authentication.getName());
		return apiUtil.buildSuccessResponse(
				HttpStatus.OK, SuccessCode.SUCCESS.toString(),
				"Approved students retrieved successfully", students);
	}
}
