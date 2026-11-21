package com.sakura.languageschool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.api.common.ApiErrorResponse;
import com.sakura.languageschool.api.common.SuccessCode;
import com.sakura.languageschool.model.dto.AuthResponse;
import com.sakura.languageschool.model.dto.CurrentUserResponse;
import com.sakura.languageschool.model.dto.LoginRequest;
import com.sakura.languageschool.model.dto.StudentRequest;
import com.sakura.languageschool.model.dto.StudentResponse;
import com.sakura.languageschool.service.AuthService;
import com.sakura.languageschool.service.StudentService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final StudentService studentService;
    private final AuthService authService;
    private final ApiUtil apiUtil;

    public AuthController(
            StudentService studentService,
            AuthService authService,
            ApiUtil apiUtil) {
        this.studentService = studentService;
        this.authService = authService;
        this.apiUtil = apiUtil;
    }

    // Student registration
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody StudentRequest request) {

        StudentResponse response =
                studentService.registerStudent(request);

        return apiUtil.buildSuccessResponse(
                HttpStatus.CREATED,
                SuccessCode.SUCCESS.toString(),
                "Student registered successfully",
                response
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            AuthResponse response = authService.login(request);

            return apiUtil.buildSuccessResponse(
                    HttpStatus.OK,
                    SuccessCode.SUCCESS.toString(),
                    "Login successful",
                    response
            );
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponse(
                            HttpStatus.UNAUTHORIZED,
                            "AUTHENTICATION_FAILED",
                            "Invalid username or password"));
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/me")
    public ResponseEntity<?> currentUser(Authentication authentication) {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK,
                SuccessCode.SUCCESS.toString(),
                "Current user retrieved successfully",
                new CurrentUserResponse(
                        authentication.getName(),
                        authentication.getAuthorities().stream()
                                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                                .sorted()
                                .toList()));
    }
}

