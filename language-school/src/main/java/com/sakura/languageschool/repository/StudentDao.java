package com.sakura.languageschool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakura.languageschool.model.StudentProfile;

public interface StudentDao extends JpaRepository<StudentProfile, Long>{
	List<StudentProfile> findByUser_EnabledTrue();

    Optional<StudentProfile> findByIdAndUser_EnabledTrue(Long id);

    Optional<StudentProfile> findByUser_Username(String username);
    
    
}
