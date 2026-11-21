package com.sakura.languageschool.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentResponse {
	private Long id;
    private Long userId;
    private String username;
    private String email;

    private String fullName;
    private String phone;
    private String address;
    private boolean enabled;
}
