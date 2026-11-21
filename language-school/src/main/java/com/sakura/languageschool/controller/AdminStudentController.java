package com.sakura.languageschool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.AdminStudentUpdateRequest;
import com.sakura.languageschool.service.StudentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/students")
public class AdminStudentController {
    private final StudentService studentService;
    private final ApiUtil apiUtil;

    public AdminStudentController(StudentService studentService, ApiUtil apiUtil) {
        this.studentService = studentService;
        this.apiUtil = apiUtil;
    }

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "Students retrieved successfully",
                studentService.getAllStudentsForAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "Student retrieved successfully",
                studentService.getStudentByIdForAdmin(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody AdminStudentUpdateRequest request) {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "Student updated successfully",
                studentService.updateStudentForAdmin(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateStudent(@PathVariable Long id) {
        studentService.deactivateStudent(id);
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "Student deactivated successfully", null);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateStudent(@PathVariable Long id) {
        studentService.activateStudent(id);
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "Student activated successfully", null);
    }
}
