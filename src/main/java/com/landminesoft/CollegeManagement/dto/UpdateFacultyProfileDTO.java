package com.landminesoft.CollegeManagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFacultyProfileDTO {

    @Size(max = 15, message = "Phone must be at most 15 characters")
    private String phone;

    @Size(max = 100, message = "Qualification must be at most 100 characters")
    private String qualification;

    @Min(value = 0, message = "Experience years must be non-negative")
    private Integer experienceYears;
}
