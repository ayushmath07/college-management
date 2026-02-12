package com.landminesoft.CollegeManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private Integer semester;

    @Column(name = "academic_year", length = 10)
    private String academicYear;

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, DROPPED, COMPLETED

    @Column(name = "enrolled_date")
    private LocalDateTime enrolledDate;
}
