
package com.sakura.languageschool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.StudentRequest;
import com.sakura.languageschool.model.dto.StudentResponse;
import com.sakura.languageschool.service.StudentService;


@RestController
@RequestMapping("/api/students")
@Slf4j
public class StudentController {
	
	@Autowired
	StudentService studentService;

    @Autowired
    ApiUtil apiUtil;

    // Get all students
    @GetMapping
    public ResponseEntity<?> getAllStudents() {

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Students retrieved successfully",
                studentService.getAllStudents()
        );
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(
            @PathVariable Long id) {

        StudentResponse response =
                studentService.getStudentById(id);

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Student retrieved successfully",
                response
        );
    }

    // Update student profile
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {

        StudentResponse response =
                studentService.updateStudent(id, request);

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Student updated successfully",
                response
        );
    }

    // Deactivate student
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateStudent(
            @PathVariable Long id) {

        studentService.deactivateStudent(id);

        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Student deactivated successfully",
                null
        );
    }
}
