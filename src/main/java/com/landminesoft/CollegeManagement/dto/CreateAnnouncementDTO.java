package com.landminesoft.CollegeManagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAnnouncementDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Target audience is required (STUDENT/FACULTY/ALL)")
    private String targetAudience;

    private LocalDate expiresAt;
}
