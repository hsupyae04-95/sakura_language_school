package com.sakura.languageschool.model.dto;

import java.time.LocalDateTime;

import com.sakura.languageschool.model.enums.EnrollmentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentResponse {
	private Long id;

    private Long studentId;
    private String studentName;

    private Long batchId;
    private String batchName;

    private Long courseId;
    private String courseName;

    private EnrollmentStatus status;

    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime approvedAt;

    private Long reviewedBy;
    private String adminNote;
}
