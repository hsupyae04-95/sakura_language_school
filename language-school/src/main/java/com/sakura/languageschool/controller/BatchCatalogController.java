package com.sakura.languageschool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.service.ClassBatchService;

@RestController
@RequestMapping("/api/batches")
public class BatchCatalogController {
	private final ClassBatchService classBatchService;
	private final ApiUtil apiUtil;

	public BatchCatalogController(ClassBatchService classBatchService, ApiUtil apiUtil) {
		this.classBatchService = classBatchService;
		this.apiUtil = apiUtil;
	}

	@GetMapping
	public ResponseEntity<?> getAllBatches() {
		return apiUtil.buildSuccessResponse(
				HttpStatus.OK, SuccessCode.SUCCESS.toString(),
				"Batches retrieved successfully", classBatchService.getAllBatches());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getBatchById(@PathVariable Long id) {
		return apiUtil.buildSuccessResponse(
				HttpStatus.OK, SuccessCode.SUCCESS.toString(),
				"Batch retrieved successfully", classBatchService.getBatchById(id));
	}
}
