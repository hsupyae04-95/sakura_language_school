package com.sakura.languageschool.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakura.languageschool.bean.Mapper;
import com.sakura.languageschool.model.StudentProfile;
import com.sakura.languageschool.model.Users;
import com.sakura.languageschool.model.dto.AuthRequest;
import com.sakura.languageschool.model.dto.StudentRequest;
import com.sakura.languageschool.model.dto.StudentResponse;
import com.sakura.languageschool.model.dto.AdminStudentUpdateRequest;
import com.sakura.languageschool.repository.UserDao;
import com.sakura.languageschool.repository.StudentDao;
import com.sakura.languageschool.security.SecurityUtil;
import com.sakura.languageschool.service.AuthService;
import com.sakura.languageschool.service.StudentService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class StudentServiceImpl implements StudentService{
	private final StudentDao studentDao;
	private final AuthService authService;
	private final Mapper mapper;
	private final UserDao userDao;
	private final SecurityUtil securityUtil;

	public StudentServiceImpl(
	        StudentDao studentDao,
	        AuthService authService,
	        Mapper mapper,
	        UserDao userDao,
	        SecurityUtil securityUtil) {
		this.studentDao = studentDao;
		this.authService = authService;
		this.mapper = mapper;
		this.userDao = userDao;
		this.securityUtil = securityUtil;
	}
	
	@Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {

        List<StudentProfile> students =
                studentDao.findByUser_EnabledTrue();

        return students.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {

        StudentProfile student = studentDao
                .findByIdAndUser_EnabledTrue(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Student not found"));

        return mapToResponse(student);
    }
    
    @Override
    @Transactional
    public StudentResponse registerStudent(StudentRequest request) {

        AuthRequest registerRequest = new AuthRequest();

        registerRequest.setUsername(request.getUsername());
        registerRequest.setEmail(request.getEmail());
        registerRequest.setPassword(request.getPassword());

        Users savedUser = authService.createUserAccount(
                registerRequest, "STUDENT");

        StudentProfile student = new StudentProfile();

        student.setUser(savedUser);
        student.setFullName(request.getFullName());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());

        StudentProfile savedStudent = studentDao.save(student);

        return mapToResponse(savedStudent);
    }



    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {

        StudentProfile student = studentDao.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Student not found"));

        if (!student.getUser().isEnabled()) {
            throw new IllegalStateException(
                    "Cannot update an inactive student");
        }

        mapper.map(request, student);

        StudentProfile updatedStudent = studentDao.save(student);

        return mapToResponse(updatedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudentsForAdmin() {
        return studentDao.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentByIdForAdmin(Long id) {
        StudentProfile student = studentDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        return mapToResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse updateStudentForAdmin(
            Long id, AdminStudentUpdateRequest request) {
        StudentProfile student = studentDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Users user = student.getUser();
        userDao.findByUsername(request.getUsername())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Username already exists");
                });
        userDao.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email already exists");
                });

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(securityUtil.getHash(request.getPassword()));
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        student.setFullName(request.getFullName());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());

        return mapToResponse(studentDao.save(student));
    }

    @Override
    @Transactional
    public void deactivateStudent(Long id) {

        StudentProfile student = studentDao.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Student not found"));

        Users user = student.getUser();

        user.setEnabled(false);
    }

    @Override
    @Transactional
    public void activateStudent(Long id) {
        StudentProfile student = studentDao.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Student not found"));
        student.getUser().setEnabled(true);
    }

    private StudentResponse mapToResponse(StudentProfile student) {

        StudentResponse response =
                mapper.map(student, StudentResponse.class);

        // Map nested User properties explicitly
        response.setUserId(student.getUser().getId());
        response.setUsername(student.getUser().getUsername());
        response.setEmail(student.getUser().getEmail());
        response.setEnabled(student.getUser().isEnabled());

        return response;
    }
}
