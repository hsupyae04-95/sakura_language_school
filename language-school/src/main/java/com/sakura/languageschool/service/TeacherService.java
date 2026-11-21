package com.sakura.languageschool.service;

import java.util.List;

import com.sakura.languageschool.model.dto.TeacherRequest;
import com.sakura.languageschool.model.dto.TeacherResponse;
import com.sakura.languageschool.model.dto.AdminTeacherUpdateRequest;

public interface TeacherService {
	List<TeacherResponse> getAllTeachers();

    TeacherResponse getTeacherById(Long id);

    TeacherResponse createTeacher(TeacherRequest request);

    TeacherResponse updateTeacher(Long id, TeacherRequest request);

    TeacherResponse updateTeacher(Long id, AdminTeacherUpdateRequest request);

    void deactivateTeacher(Long id);

    void activateTeacher(Long id);
}
