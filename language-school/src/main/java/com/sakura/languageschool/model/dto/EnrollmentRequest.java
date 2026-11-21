package com.sakura.languageschool.model.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentRequest {
    @Positive
    private Long studentId;

    @NotNull
    @Positive
    private Long batchId;

    @NotNull(message = "Confirmation is required before applying")
    @AssertTrue(message = "You must confirm the application before submitting")
    private Boolean confirmed;
}
