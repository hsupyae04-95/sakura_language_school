package com.sakura.languageschool.service;

import java.util.List;

import com.sakura.languageschool.model.dto.AuthRequest;
import com.sakura.languageschool.model.dto.UpdateUserRequest;
import com.sakura.languageschool.model.dto.UserResponse;

public interface UserService {
	
	
	List<UserResponse> getAllUsers();
	
	UserResponse getUserById(Long id);
	
	UserResponse updateUser(Long id, UpdateUserRequest request);
	
	void deleteUser(Long id);

	void activateUser(Long id);
	
}
