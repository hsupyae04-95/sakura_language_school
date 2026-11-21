package com.sakura.languageschool.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sakura.languageschool.model.Users;
import com.sakura.languageschool.repository.UserDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class UserDaoTest {
	@Autowired
	UserDao userDao;
	
	//@Test
	public void testSaveUser() {
	    Users user = new Users();

	    user.setUsername("testuser");
	    user.setEmail("test@example.com");
	    user.setPassword("test123456");

	    userDao.save(user);
	}
}
