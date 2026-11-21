package com.sakura.languageschool.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentRequest {
	private String username;
    private String email;
    private String password;

    @NotBlank
    @Size(max = 150)
    private String fullName;

    private String phone;
    private String address;
}
