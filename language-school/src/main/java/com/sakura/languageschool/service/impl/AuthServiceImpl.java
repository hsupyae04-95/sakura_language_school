package com.sakura.languageschool.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.sakura.languageschool.bean.Mapper;
import com.sakura.languageschool.model.Roles;
import com.sakura.languageschool.model.UserRole;
import com.sakura.languageschool.model.UserRoleId;
import com.sakura.languageschool.model.Users;
import com.sakura.languageschool.model.dto.AuthResponse;
import com.sakura.languageschool.model.dto.AuthRequest;
import com.sakura.languageschool.model.dto.LoginRequest;
import com.sakura.languageschool.repository.RoleDao;
import com.sakura.languageschool.repository.UserDao;
import com.sakura.languageschool.repository.UserRoleDao;
import com.sakura.languageschool.security.JwtProperties;
import com.sakura.languageschool.security.JwtService;
import com.sakura.languageschool.security.SecurityUtil;
import com.sakura.languageschool.service.AuthService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserDao userDao;
	private final Mapper mapper;
	private final RoleDao roleDao;
	private final UserRoleDao userRoleDao;
	private final SecurityUtil securityUtil;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;

	public AuthServiceImpl(
			UserDao userDao,
			Mapper mapper,
			RoleDao roleDao,
			UserRoleDao userRoleDao,
			SecurityUtil securityUtil,
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			JwtProperties jwtProperties) {
		this.userDao = userDao;
		this.mapper = mapper;
		this.roleDao = roleDao;
		this.userRoleDao = userRoleDao;
		this.securityUtil = securityUtil;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.jwtProperties = jwtProperties;
	}

	@Override
	@Transactional
	public Users createUserAccount(AuthRequest request, String roleName) {
		// 1. Check username
        if (userDao.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username already exists");
        }

        // 2. Check email
        if (userDao.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        // 3. Create user entity
        Users user = mapper.map(request, Users.class);

        // 4. Hash password
        user.setPassword(
                securityUtil.getHash(request.getPassword()));

        user.setEnabled(true);

        // 5. Save user
        Users savedUser = userDao.save(user);

        // 6. Find role
        Roles role = roleDao.findByRole(roleName)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Role not found: " + roleName));

        // 7. Create user-role relationship
        UserRoleId userRoleId = new UserRoleId();

        userRoleId.setUserId(savedUser.getId());
        userRoleId.setRoleId(role.getId());

        UserRole userRole = new UserRole();

        userRole.setId(userRoleId);
        userRole.setUser(savedUser);
        userRole.setRole(role);

        // 8. Save relationship
        userRoleDao.save(userRole);

        return savedUser;
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUsername(), request.getPassword()));

		String accessToken = jwtService.generateAccessToken(
				authentication.getName());

		return new AuthResponse(
				accessToken,
				"Bearer",
				jwtProperties.getAccessTokenTtl().toSeconds());
	}

}
