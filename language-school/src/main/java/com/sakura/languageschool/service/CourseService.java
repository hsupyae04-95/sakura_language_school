package com.sakura.languageschool.service;

import java.util.List;

import com.sakura.languageschool.model.dto.CourseRequest;
import com.sakura.languageschool.model.dto.CourseRespone;

public interface CourseService {
	List<CourseRespone> getAllCourses();
	
	CourseRespone getCourseById(Long id);
	
	CourseRespone createCourse(CourseRequest request);
	
	CourseRespone updateCourse(Long id, CourseRequest request);
	
	void deactivateCourse(Long id);
}
