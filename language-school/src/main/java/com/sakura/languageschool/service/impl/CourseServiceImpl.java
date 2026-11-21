package com.sakura.languageschool.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakura.languageschool.bean.Mapper;
import com.sakura.languageschool.model.Course;
import com.sakura.languageschool.model.dto.CourseRequest;
import com.sakura.languageschool.model.dto.CourseRespone;
import com.sakura.languageschool.repository.CourseDao;
import com.sakura.languageschool.service.CourseService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CourseServiceImpl implements CourseService{
	
	@Autowired
	CourseDao courseDao;
	
	@Autowired
	Mapper mapper;

	@Override
	@Transactional(readOnly = true)
	public List<CourseRespone> getAllCourses() {
		List<Course> courses = courseDao.findByActiveTrue();
		
		return mapper.mapList(courses, CourseRespone.class);
	}

	@Override
	@Transactional(readOnly = true)
	public CourseRespone getCourseById(Long id) {
		Course course = courseDao.findById(id)
				.filter(Course::isActive)
				.orElseThrow(() -> new EntityNotFoundException("Course not found"));
		
		return mapper.map(course, CourseRespone.class);
	}

	@Override
	@Transactional
	public CourseRespone createCourse(CourseRequest request) {
		 Course course = mapper.map(request, Course.class);
		 course.setActive(true);
		 Course savedCourse = courseDao.save(course);
		 
		 return mapper.map(savedCourse, CourseRespone.class);
	}

	@Override
	@Transactional
	public CourseRespone updateCourse(Long id, CourseRequest request) {
		Course course = courseDao.findById(id)
				.orElseThrow(()-> new EntityNotFoundException("Course not found"));
		
		if(!course.isActive()) {
			throw new IllegalStateException("Cannot update an inactive course");
		}
		
		mapper.map(request, course);
		Course updatedCourse = courseDao.save(course);
		
		return mapper.map(updatedCourse, CourseRespone.class);
	}

	@Override
	@Transactional
	public void deactivateCourse(Long id) {
		Course course = courseDao.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found"));

        course.setActive(false);

        courseDao.save(course);
		
	}

}
