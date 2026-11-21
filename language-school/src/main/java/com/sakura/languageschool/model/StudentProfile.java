package com.sakura.languageschool.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StudentProfile extends BaseEntity{
	@Column(nullable = false)
	private String fullName;
	
	@Column
	private String phone;
	
	@Column
	private String address;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private Users user;
	
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
	private Set<Enrollment> enrollments = new HashSet<>();
}
