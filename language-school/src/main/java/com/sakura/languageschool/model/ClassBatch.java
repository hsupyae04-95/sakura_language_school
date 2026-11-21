package com.sakura.languageschool.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.sakura.languageschool.model.enums.BatchStatus;
import com.sakura.languageschool.model.enums.ClassType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_course_batch_name",
	            columnNames = {"course_id", "batch_name"}
	        )
	    }
	)
public class ClassBatch extends BaseEntity{
	@Column(name = "batch_name", nullable = false, length = 100)
    private String batchName;

	@Enumerated(EnumType.STRING)
	@Column(name = "class_type", nullable = false, length = 45)
	private ClassType classType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String schedule;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)
    private BatchStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherProfile teacher;
    
    @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();
}
