package com.sakura.languageschool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakura.languageschool.model.TeacherProfile;

public interface TeacherDao extends JpaRepository<TeacherProfile, Long>{
	
	List<TeacherProfile> findByActiveTrue();
	
	Optional<TeacherProfile> findByIdAndActiveTrue(Long id);
}
