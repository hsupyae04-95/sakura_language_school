package com.sakura.languageschool.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TeacherProfile extends BaseEntity{
	@Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String specialization;

    @Column(length = 150)
    private String qualification;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean active = true;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private Users user;
    
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<ClassBatch> batches = new HashSet<>();
}
