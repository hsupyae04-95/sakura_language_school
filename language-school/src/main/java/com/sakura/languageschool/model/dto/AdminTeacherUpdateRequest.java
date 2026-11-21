package com.sakura.languageschool.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Editable teacher account and profile fields for administrators. */
@Getter
@Setter
public class AdminTeacherUpdateRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "^$|.{8,}", message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    @Size(max = 150)
    private String fullName;

    @Size(max = 30)
    private String phone;

    @Size(max = 100)
    private String specialization;

    @Size(max = 150)
    private String qualification;

    private Boolean active;
}
