package com.sakura.languageschool.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakura.languageschool.bean.Mapper;
import com.sakura.languageschool.model.TeacherProfile;
import com.sakura.languageschool.model.Users;
import com.sakura.languageschool.model.dto.AuthRequest;
import com.sakura.languageschool.model.dto.TeacherRequest;
import com.sakura.languageschool.model.dto.TeacherResponse;
import com.sakura.languageschool.model.dto.AdminTeacherUpdateRequest;
import com.sakura.languageschool.repository.TeacherDao;
import com.sakura.languageschool.repository.UserDao;
import com.sakura.languageschool.security.SecurityUtil;
import com.sakura.languageschool.service.AuthService;
import com.sakura.languageschool.service.TeacherService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TeacherServiceImpl implements TeacherService{
	private final AuthService authService;
	private final TeacherDao teacherDao;
	private final Mapper mapper;
	private final UserDao userDao;
	private final SecurityUtil securityUtil;

	public TeacherServiceImpl(
	        AuthService authService,
	        TeacherDao teacherDao,
	        Mapper mapper,
	        UserDao userDao,
	        SecurityUtil securityUtil) {
		this.authService = authService;
		this.teacherDao = teacherDao;
		this.mapper = mapper;
		this.userDao = userDao;
		this.securityUtil = securityUtil;
	}

	@Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> getAllTeachers() {

        List<TeacherProfile> teachers = teacherDao.findAll();

        return teachers.stream().map(this::mapResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {

        TeacherProfile teacher = teacherDao.findById(id)
                .filter(TeacherProfile::isActive)
                .orElseThrow(() ->
                        new EntityNotFoundException("Teacher not found"));

        return mapResponse(teacher);
    }

    @Override
    @Transactional
    public TeacherResponse createTeacher(TeacherRequest request) {

    	// 1. Prepare account request
        AuthRequest registerRequest = new AuthRequest();

        registerRequest.setUsername(request.getUsername());
        registerRequest.setEmail(request.getEmail());
        registerRequest.setPassword(request.getPassword());

        // 2. Create user account and assign TEACHER role
        Users savedUser = authService.createUserAccount(
                registerRequest, "TEACHER");

        // 3. Create teacher profile
        TeacherProfile teacher = new TeacherProfile();

        teacher.setUser(savedUser);
        teacher.setFullName(request.getFullName());
        teacher.setPhone(request.getPhone());
        teacher.setSpecialization(request.getSpecialization());
        teacher.setQualification(request.getQualification());
        teacher.setActive(true);

        // 4. Save teacher profile
        TeacherProfile savedTeacher = teacherDao.save(teacher);

        // 5. Return response
        return mapResponse(savedTeacher);
    }

    @Override
    @Transactional
    public TeacherResponse updateTeacher(Long id, TeacherRequest request) {

        TeacherProfile teacher = teacherDao.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Teacher not found"));

        if (!teacher.isActive()) {
            throw new IllegalStateException(
                    "Cannot update an inactive teacher");
        }

        mapper.map(request, teacher);

        TeacherProfile updatedTeacher = teacherDao.save(teacher);

        return mapResponse(updatedTeacher);
    }

    @Override
    @Transactional
    public TeacherResponse updateTeacher(Long id, AdminTeacherUpdateRequest request) {
        TeacherProfile teacher = teacherDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
        Users user = teacher.getUser();
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
        if (request.getActive() != null) {
            teacher.setActive(request.getActive());
            user.setEnabled(request.getActive());
        }
        teacher.setFullName(request.getFullName());
        teacher.setPhone(request.getPhone());
        teacher.setSpecialization(request.getSpecialization());
        teacher.setQualification(request.getQualification());

        return mapResponse(teacherDao.save(teacher));
    }

    @Override
    @Transactional
    public void deactivateTeacher(Long id) {

        TeacherProfile teacher = teacherDao.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Teacher not found"));

        teacher.setActive(false);

        if (teacher.getUser() != null) {
            teacher.getUser().setEnabled(false);
        }

        teacherDao.save(teacher);
    }

    @Override
    @Transactional
    public void activateTeacher(Long id) {
        TeacherProfile teacher = teacherDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        teacher.setActive(true);
        if (teacher.getUser() != null) {
            teacher.getUser().setEnabled(true);
        }
        teacherDao.save(teacher);
    }

    private TeacherResponse mapResponse(TeacherProfile teacher) {
        TeacherResponse response = mapper.map(teacher, TeacherResponse.class);
        if (teacher.getUser() != null) {
            response.setUserId(teacher.getUser().getId());
            response.setUsername(teacher.getUser().getUsername());
            response.setEmail(teacher.getUser().getEmail());
        }
        return response;
    }

}
