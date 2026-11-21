package com.sakura.languageschool.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;

import com.sakura.languageschool.api.common.ApiSuccessResponse;
import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.TeacherRequest;
import com.sakura.languageschool.model.dto.TeacherResponse;
import com.sakura.languageschool.model.dto.AdminTeacherUpdateRequest;
import com.sakura.languageschool.service.TeacherService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/admin/teachers")
public class TeacherController {
	private final ApiUtil apiUtil;
	private final TeacherService teacherService;

	public TeacherController(ApiUtil apiUtil, TeacherService teacherService) {
		this.apiUtil = apiUtil;
		this.teacherService = teacherService;
	}

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<TeacherResponse>>> getAllTeachers() {

        List<TeacherResponse> teachers = teacherService.getAllTeachers();

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Teachers retrieved successfully",
                teachers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<TeacherResponse>> getTeacherById(
            @PathVariable Long id) {

        TeacherResponse teacher = teacherService.getTeacherById(id);

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Teacher retrieved successfully",
                teacher);
    }

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<TeacherResponse>> createTeacher(
            @Valid @RequestBody TeacherRequest request) {

        TeacherResponse teacher = teacherService.createTeacher(request);

        return apiUtil.buildSuccessResponse(
                HttpStatus.CREATED,
                SuccessCode.SUCCESS.toString(),
                "Teacher created successfully",
                teacher);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<TeacherResponse>> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody AdminTeacherUpdateRequest request) {

        TeacherResponse teacher = teacherService.updateTeacher(id, request);

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Teacher updated successfully",
                teacher);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<Void>> deactivateTeacher(
            @PathVariable Long id) {
        teacherService.deactivateTeacher(id);
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Teacher account deactivated successfully",
                null);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<Void>> activateTeacher(
            @PathVariable Long id) {
        teacherService.activateTeacher(id);
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Teacher account activated successfully",
                null);
    }

}
