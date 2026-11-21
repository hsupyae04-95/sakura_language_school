package com.sakura.languageschool.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakura.languageschool.bean.Mapper;
import com.sakura.languageschool.model.Users;
import com.sakura.languageschool.model.dto.UpdateUserRequest;
import com.sakura.languageschool.model.dto.UserResponse;
import com.sakura.languageschool.repository.RoleDao;
import com.sakura.languageschool.repository.UserDao;
import com.sakura.languageschool.repository.UserRoleDao;
import com.sakura.languageschool.security.SecurityUtil;
import com.sakura.languageschool.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	RoleDao roleDao;
	
	@Autowired
	UserRoleDao userRoleDao;

	@Autowired
	Mapper mapper;
	
	@Autowired
	SecurityUtil securityUtil;
	
	



	@Override
	public List<UserResponse> getAllUsers() {
		List<Users> users = userDao.findAll();
		return mapper.mapList(users, UserResponse.class);
	}

	@Override
	public UserResponse getUserById(Long id) {
		Users user = userDao.findById(id)
				.orElseThrow(() -> 
				new EntityNotFoundException("User not found"));
		
		return mapper.map(user, UserResponse.class);
	}

	@Override
	@Transactional
	public UserResponse updateUser(Long id, UpdateUserRequest request) {
		Users user = userDao.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		userDao.findByUsername(request.getUsername())
        .filter(existing -> !existing.getId().equals(id))
        .ifPresent(existing -> {
            throw new IllegalArgumentException(
                    "Username already exists");
        });
		
		userDao.findByEmail(request.getEmail())
			.filter(existing -> !existing.getId().equals(id))
			.ifPresent(existing -> {
				throw new IllegalArgumentException(
	                    "email already exists");
			});
		
		mapper.map(request, user);
		
		Users updatedUser = userDao.save(user);
		
		return mapper.map(updatedUser, UserResponse.class);
		
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {
		Users user = userDao.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		user.setEnabled(false);
		userDao.save(user);
	}

	@Override
	@Transactional
	public void activateUser(Long id) {
		Users user = userDao.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		user.setEnabled(true);
		userDao.save(user);
	}

}
