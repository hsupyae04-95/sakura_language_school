package com.sakura.languageschool.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Users extends BaseEntity{
	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(unique = true)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@Column(columnDefinition = "TINYINT(1)")
	private boolean enabled;
	
	@OneToMany(mappedBy = "user")
	private Set<UserRole> userRoles = new HashSet<>();
	
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private StudentProfile studentProfile;
	
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private TeacherProfile teacherProfile;

	@OneToMany(mappedBy = "reviewedBy", fetch = FetchType.LAZY)
	private Set<Enrollment> reviewedEnrollments = new HashSet<>();
	
}
