package com.sakura.languageschool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.ApiSuccessResponse;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.CourseRequest;
import com.sakura.languageschool.model.dto.CourseRespone;
import com.sakura.languageschool.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/courses")
public class CourseController {
	private final CourseService courseService;
	private final ApiUtil apiUtil;

	public CourseController(CourseService courseService, ApiUtil apiUtil) {
		this.courseService = courseService;
		this.apiUtil = apiUtil;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiSuccessResponse<CourseRespone>> getCourse(
			@PathVariable Long id) {
		return apiUtil.buildSuccessResponse(
				HttpStatus.OK, SuccessCode.SUCCESS.toString(),
				"Course retrieved successfully", courseService.getCourseById(id));
	}
	
	@PostMapping
	public ResponseEntity<ApiSuccessResponse<CourseRespone>> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
		CourseRespone savedCourse = courseService.createCourse(courseRequest);
		
		return apiUtil.buildSuccessResponse(
                HttpStatus.CREATED,
                SuccessCode.SUCCESS.toString(),
                "Course created successfully",
                savedCourse
        );
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiSuccessResponse<CourseRespone>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {

        CourseRespone updatedCourse = courseService.updateCourse(id, request);

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Course updated successfully",
                updatedCourse
        );
    }
	
	@PatchMapping("/{id}/deactivate")
	public ResponseEntity<ApiSuccessResponse<Void>> deactivateCourse(
            @PathVariable Long id) {

        courseService.deactivateCourse(id);

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Course deactivated successfully",
                null
        );
    }
}
