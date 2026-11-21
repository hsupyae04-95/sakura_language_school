package com.sakura.languageschool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sakura.languageschool.model.UserRole;
import com.sakura.languageschool.model.UserRoleId;

@Repository
public interface UserRoleDao extends JpaRepository<UserRole, UserRoleId>{
	@Query("select ur from UserRole ur join fetch ur.role where ur.id.userId = :userId")
	List<UserRole> findByUserId(@Param("userId") Long userId);
	
	boolean existsByUserIdAndRoleId(Long userId, Long roleId);
}
