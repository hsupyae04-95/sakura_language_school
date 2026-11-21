package com.sakura.languageschool.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.languageschool.api.common.ApiSuccessResponse;
import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.CourseRespone;
import com.sakura.languageschool.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseCatalogController {
	private final CourseService courseService;
	private final ApiUtil apiUtil;

	public CourseCatalogController(CourseService courseService, ApiUtil apiUtil) {
		this.courseService = courseService;
		this.apiUtil = apiUtil;
	}

	@GetMapping
	public ResponseEntity<ApiSuccessResponse<List<CourseRespone>>> getAllCourses() {
		return apiUtil.buildSuccessResponse(
				HttpStatus.OK,
				SuccessCode.SUCCESS.toString(),
				"Courses are available",
				courseService.getAllCourses());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiSuccessResponse<CourseRespone>> getCourseById(
			@PathVariable Long id) {
		return apiUtil.buildSuccessResponse(
				HttpStatus.OK,
				SuccessCode.SUCCESS.toString(),
				"Course retrieved successfully",
				courseService.getCourseById(id));
	}
}
