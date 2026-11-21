package com.sakura.languageschool.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sakura.languageschool.model.dto.AuthRequest;
import com.sakura.languageschool.model.dto.UserResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class AuthTest {
	@Autowired
	UserService userService;
	
	//@Test
//	void testAddUser() {
//		RegisterRequest user = new RegisterRequest();
//		user.setUsername("student1");
//		user.setPassword("student246");
//		user.setEmail("student246@gmail.com");
//		UserResponse response = userService.register(user);
//
//	    System.out.println(response);
//		
//	}
	
	//@Test
	void testGetUsers() {
//		List<UserResponse> users = userService.getAllUsers();
//		
//		users.forEach(System.err::println);
		
		userService.deleteUser(3L);
	}
}
