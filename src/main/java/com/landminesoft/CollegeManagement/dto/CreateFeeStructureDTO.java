package com.landminesoft.CollegeManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateFeeStructureDTO {

    @NotBlank(message = "Branch is required")
    private String branch;

    @NotNull(message = "Semester is required")
    private Integer semester;

    private BigDecimal tuitionFee;
    private BigDecimal hostelFee;
    private BigDecimal libraryFee;
    private BigDecimal labFee;

    private LocalDate dueDate;
}
