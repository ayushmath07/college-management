package com.landminesoft.CollegeManagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSubjectDTO {

    @NotBlank(message = "Subject code is required")
    private String subjectCode;

    @NotBlank(message = "Subject name is required")
    private String subjectName;

    @NotBlank(message = "Branch is required")
    private String branch;

    @NotNull(message = "Semester is required")
    @Min(1)
    private Integer semester;

    @NotNull(message = "Credits required")
    @Min(1)
    private Integer credits;

    private Integer theoryMarks;
    private Integer practicalMarks;
}
