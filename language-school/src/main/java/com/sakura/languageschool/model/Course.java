package com.sakura.languageschool.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Course extends BaseEntity{
	@Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String level;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(precision = 10, scale = 2)
    private BigDecimal fee;
    

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean active = true;

    
}
