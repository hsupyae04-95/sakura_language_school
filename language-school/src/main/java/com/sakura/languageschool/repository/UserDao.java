package com.sakura.languageschool.repository;

import org.springframework.stereotype.Repository;

import com.sakura.languageschool.model.Users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserDao extends JpaRepository<Users, Long>{
	Optional<Users> findByUsername(String username);
	
	Optional<Users> findByEmail(String email);
	
	boolean existsByUsername(String username);
	
	boolean existsByEmail(String email);
}
