package com.sakura.languageschool.model.dto;

import com.sakura.languageschool.model.enums.EnrollmentStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewEnrollmentRequest {
	@NotNull
    private EnrollmentStatus status;

    @Size(max = 500)
    private String adminNote;
}
