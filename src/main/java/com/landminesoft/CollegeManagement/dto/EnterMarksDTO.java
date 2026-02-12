package com.landminesoft.CollegeManagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnterMarksDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private Integer theoryMarks;
    private Integer practicalMarks;
    private String grade;
}
