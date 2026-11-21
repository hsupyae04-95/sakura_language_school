package com.sakura.languageschool.model.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseRespone {
	private Long id;
    private String name;
    private String level;
    private String description;
    private Integer durationMonths;
    private BigDecimal fee;
    private boolean active;
}
