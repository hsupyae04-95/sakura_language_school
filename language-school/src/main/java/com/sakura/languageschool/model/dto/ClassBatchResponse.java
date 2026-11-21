package com.sakura.languageschool.model.dto;

import java.time.LocalDate;

import com.sakura.languageschool.model.enums.BatchStatus;
import com.sakura.languageschool.model.enums.ClassType;

import lombok.Data;

@Data
public class ClassBatchResponse {
	private Long id;

    private Long courseId;
    private String courseName;

    private Long teacherId;
    private String teacherName;

    private String batchName;
    private ClassType classType;

    private LocalDate startDate;
    private LocalDate endDate;

    private String schedule;
    private Integer capacity;

    private long enrolledCount;
    private long availableSeats;
    private boolean available;

    private BatchStatus status;
}
