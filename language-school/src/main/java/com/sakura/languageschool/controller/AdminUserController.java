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
import com.sakura.languageschool.model.dto.UpdateUserRequest;
import com.sakura.languageschool.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserService userService;
    private final ApiUtil apiUtil;

    public AdminUserController(UserService userService, ApiUtil apiUtil) {
        this.userService = userService;
        this.apiUtil = apiUtil;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "Users retrieved successfully", userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "User retrieved successfully", userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "User updated successfully", userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "User deactivated successfully", null);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return apiUtil.buildSuccessResponse(
                HttpStatus.OK, SuccessCode.SUCCESS.toString(),
                "User activated successfully", null);
    }
}
