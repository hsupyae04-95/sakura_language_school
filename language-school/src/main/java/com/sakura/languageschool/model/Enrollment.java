package com.sakura.languageschool.model;

import java.time.LocalDateTime;

import com.sakura.languageschool.model.enums.EnrollmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_student_batch",
            columnNames = {"student_id", "batch_id"}
        )
    })
public class Enrollment extends BaseEntity{
	
	@Column
	private LocalDateTime appliedAt;

	@Column
    private LocalDateTime reviewAt;

    @Column
    private LocalDateTime approvedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)
    private EnrollmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Users reviewedBy;
    
    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private ClassBatch batch;

}
