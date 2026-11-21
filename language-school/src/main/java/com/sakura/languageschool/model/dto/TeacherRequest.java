package com.sakura.languageschool.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherRequest {
	
	// User account
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8)
    private String password;
    
	@NotBlank(message = "Full name is required")
    @Size(max = 150)
    private String fullName;

    @Size(max = 30)
    private String phone;

    @Size(max = 100)
    private String specialization;

    @Size(max = 150)
    private String qualification;
}
