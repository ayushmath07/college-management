package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.dto.EnrollStudentDTO;
import com.landminesoft.CollegeManagement.dto.MakeFeePaymentDTO;
import com.landminesoft.CollegeManagement.dto.UpdateStudentProfileDTO;
import com.landminesoft.CollegeManagement.entity.Enrollment;
import com.landminesoft.CollegeManagement.entity.FeePayment;
import com.landminesoft.CollegeManagement.entity.Marks;
import com.landminesoft.CollegeManagement.security.CustomUserDetails;
import com.landminesoft.CollegeManagement.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Student endpoints (requires STUDENT role)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final ProfileService profileService;
    private final EnrollmentService enrollmentService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;
    private final FeeService feeService;

    @GetMapping("/profile")
    @Operation(summary = "Get current student's profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        return ResponseEntity.ok(profileService.getStudentProfile(getCurrentUser().getId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current student's profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@Valid @RequestBody UpdateStudentProfileDTO dto) {
        return ResponseEntity.ok(profileService.updateStudentProfile(getCurrentUser().getId(), dto));
    }

    @PostMapping("/enroll")
    @Operation(summary = "Enroll in a course")
    public ResponseEntity<Enrollment> enroll(@Valid @RequestBody EnrollStudentDTO dto) {
        Enrollment enrollment = enrollmentService.enrollStudent(getCurrentUser().getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    @GetMapping("/enrollments")
    @Operation(summary = "Get all enrollments")
    public ResponseEntity<List<Enrollment>> getEnrollments() {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(getCurrentUser().getId()));
    }

    @PutMapping("/enrollments/{id}/drop")
    @Operation(summary = "Drop an enrollment")
    public ResponseEntity<Enrollment> dropEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.dropEnrollment(getCurrentUser().getId(), id));
    }

    @GetMapping("/attendance")
    @Operation(summary = "Get attendance summary")
    public ResponseEntity<List<Map<String, Object>>> getAttendance() {
        return ResponseEntity.ok(attendanceService.getAttendanceSummary(getCurrentUser().getId()));
    }

    @GetMapping("/marks")
    @Operation(summary = "Get all marks")
    public ResponseEntity<List<Marks>> getMarks() {
        return ResponseEntity.ok(marksService.getByStudent(getCurrentUser().getId()));
    }

    @GetMapping("/fees")
    @Operation(summary = "Get fee payment history")
    public ResponseEntity<List<FeePayment>> getFeePayments() {
        return ResponseEntity.ok(feeService.getStudentPayments(getCurrentUser().getId()));
    }

    @PostMapping("/fees/pay")
    @Operation(summary = "Make a fee payment")
    public ResponseEntity<FeePayment> payFee(@Valid @RequestBody MakeFeePaymentDTO dto) {
        FeePayment payment = feeService.makePayment(getCurrentUser().getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) auth.getPrincipal();
    }
}
