package com.landminesoft.CollegeManagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollStudentDTO {

    @NotNull(message = "Course ID is required")
    private Long courseId;
}
