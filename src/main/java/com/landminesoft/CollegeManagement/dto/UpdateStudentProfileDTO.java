package com.landminesoft.CollegeManagement.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentProfileDTO {

    @Size(max = 15, message = "Phone must be at most 15 characters")
    private String phone;

    private String address;

    @Size(max = 50, message = "City must be at most 50 characters")
    private String city;

    @Pattern(regexp = "^[0-9]{5,10}$", message = "Pincode must be 5-10 digits")
    private String pincode;

    private LocalDate dob;
}
