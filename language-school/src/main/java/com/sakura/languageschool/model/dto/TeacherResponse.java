package com.sakura.languageschool.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherResponse {
	private Long id;
	private Long userId;
	private String username;
	private String email;
    private String fullName;
    private String phone;
    private String specialization;
    private String qualification;
    private boolean active;
}
