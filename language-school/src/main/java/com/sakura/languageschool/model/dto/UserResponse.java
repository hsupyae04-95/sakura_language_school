package com.sakura.languageschool.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserResponse {
	private Long id;
    private String username;
    private String email;
    private boolean enabled;
}
