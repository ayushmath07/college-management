package com.landminesoft.CollegeManagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MakeFeePaymentDTO {

    @NotNull(message = "Fee structure ID is required")
    private Long feeStructureId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private String transactionId;
}
