package com.landminesoft.CollegeManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MarkAttendanceDTO {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Date is required")
    private LocalDate classDate;

    @NotNull(message = "Records are required")
    private List<AttendanceEntry> records;

    @Data
    public static class AttendanceEntry {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotBlank(message = "Status is required (PRESENT/ABSENT/LEAVE)")
        private String status;
    }
}
