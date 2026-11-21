package com.sakura.languageschool.model;



import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserRole {
	
	@EmbeddedId
	private UserRoleId id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
	@JoinColumn(name = "user_id")
	private Users user;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
	@JoinColumn(name = "role_id")
	private Roles role;
	
	
}

