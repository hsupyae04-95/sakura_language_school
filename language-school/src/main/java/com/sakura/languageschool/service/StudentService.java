package com.sakura.languageschool.service;

import java.util.List;

import com.sakura.languageschool.model.dto.StudentRequest;
import com.sakura.languageschool.model.dto.StudentResponse;
import com.sakura.languageschool.model.dto.AdminStudentUpdateRequest;

public interface StudentService {
	List<StudentResponse> getAllStudents();

    StudentResponse getStudentById(Long id);
    
    StudentResponse registerStudent(StudentRequest request);

    StudentResponse updateStudent(Long id, StudentRequest request);

    List<StudentResponse> getAllStudentsForAdmin();

    StudentResponse getStudentByIdForAdmin(Long id);

    StudentResponse updateStudentForAdmin(Long id, AdminStudentUpdateRequest request);

    void deactivateStudent(Long id);

    void activateStudent(Long id);
}
