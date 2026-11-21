package com.sakura.languageschool.service;

import com.sakura.languageschool.model.Users;
import com.sakura.languageschool.model.dto.AuthResponse;
import com.sakura.languageschool.model.dto.AuthRequest;
import com.sakura.languageschool.model.dto.LoginRequest;

public interface AuthService {
//	UserResponse register(AuthRequest request);
	
    Users createUserAccount(AuthRequest request, String roleName);

    AuthResponse login(LoginRequest request);

}
