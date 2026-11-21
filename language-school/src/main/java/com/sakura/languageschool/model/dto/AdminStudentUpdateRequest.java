package com.sakura.languageschool.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * The complete editable student record used by administrators.
 * A blank password keeps the current password unchanged.
 */
@Getter
@Setter
public class AdminStudentUpdateRequest {

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

    private String phone;
    private String address;
    private Boolean enabled;
}
