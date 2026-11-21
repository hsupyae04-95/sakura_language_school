package com.sakura.languageschool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sakura.languageschool.model.Roles;
import java.util.List;


@Repository
public interface RoleDao extends JpaRepository<Roles, Long>{
	Optional<Roles> findByRole(String role);
}
