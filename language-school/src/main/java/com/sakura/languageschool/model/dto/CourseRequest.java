package com.sakura.languageschool.model.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseRequest {
	@NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String level;

    private String description;

    @Positive
    private Integer durationMonths;

    @DecimalMin(value = "0.0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal fee;
}
