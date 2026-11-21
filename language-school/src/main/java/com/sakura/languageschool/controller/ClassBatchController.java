package com.sakura.languageschool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.ClassBatchRequest;
import com.sakura.languageschool.model.dto.ClassBatchResponse;
import com.sakura.languageschool.service.ClassBatchService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/admin/batches")
public class ClassBatchController {
	private final ClassBatchService classBatchService;
	private final ApiUtil apiUtil;

	public ClassBatchController(ClassBatchService classBatchService, ApiUtil apiUtil) {
		this.classBatchService = classBatchService;
		this.apiUtil = apiUtil;
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getBatch(@PathVariable Long id) {
		return apiUtil.buildSuccessResponse(
				HttpStatus.OK, SuccessCode.SUCCESS.toString(),
				"Batch retrieved successfully", classBatchService.getBatchById(id));
	}

    // Create batch
    @PostMapping
    public ResponseEntity<?> createBatch(
            @Valid @RequestBody ClassBatchRequest request) {

        ClassBatchResponse response =
                classBatchService.createBatch(request);

        return apiUtil.buildSuccessResponse(
                HttpStatus.CREATED,
                SuccessCode.SUCCESS.toString(),
                "Batch created successfully",
                response
        );
    }

    // Update batch
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBatch(
            @PathVariable Long id,
            @Valid @RequestBody ClassBatchRequest request) {

        ClassBatchResponse response =
                classBatchService.updateBatch(id, request);

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Batch updated successfully",
                response
        );
    }

}
