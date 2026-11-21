package com.sakura.languageschool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sakura.languageschool.model.Course;

@Repository
public interface CourseDao extends JpaRepository<Course, Long>{
	
	List<Course> findByActiveTrue();
	
	Optional<Course> findByIdAndActiveTrue(Long id);
}
