package com.sakura.languageschool.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Roles extends BaseEntity{
	@Column(nullable = false, unique = true)
	private String role;
	
	@OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = new HashSet<>();
}
