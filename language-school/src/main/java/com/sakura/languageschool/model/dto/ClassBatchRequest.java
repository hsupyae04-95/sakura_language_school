package com.sakura.languageschool.model.dto;

import java.time.LocalDate;

import com.sakura.languageschool.model.enums.ClassType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClassBatchRequest {
	 @NotNull
	    @Positive
	    private Long courseId;

	    // Teacher assignment is optional
	    private Long teacherId;

	    @NotBlank
	    @Size(max = 100)
	    private String batchName;

	    @NotNull
	    private ClassType classType;

	    @NotNull
	    @FutureOrPresent
	    private LocalDate startDate;

	    @NotNull
	    private LocalDate endDate;

	    @NotBlank
	    @Size(max = 255)
	    private String schedule;

	    @NotNull
	    @Min(1)
	    private Integer capacity;
}
