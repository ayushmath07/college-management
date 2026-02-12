package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.dto.*;
import com.landminesoft.CollegeManagement.entity.*;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin-only endpoints (requires ADMIN role)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final SubjectService subjectService;
    private final CourseService courseService;
    private final FeeService feeService;
    private final AnnouncementService announcementService;

    // --- Subject management ---

    @PostMapping("/subjects")
    @Operation(summary = "Create a new subject")
    public ResponseEntity<Subject> createSubject(@Valid @RequestBody CreateSubjectDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.createSubject(dto));
    }

    @GetMapping("/subjects")
    @Operation(summary = "List all subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/subjects/filter")
    @Operation(summary = "Get subjects by branch and semester")
    public ResponseEntity<List<Subject>> getSubjectsByBranchAndSemester(
            @RequestParam String branch, @RequestParam Integer semester) {
        return ResponseEntity.ok(subjectService.getByBranchAndSemester(branch, semester));
    }

    // --- Course management ---

    @PostMapping("/courses")
    @Operation(summary = "Create a course (assign faculty to subject)")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CreateCourseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(dto));
    }

    @GetMapping("/courses")
    @Operation(summary = "List all courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAll());
    }

    // --- Fee management ---

    @PostMapping("/fees/structures")
    @Operation(summary = "Create a fee structure")
    public ResponseEntity<FeeStructure> createFeeStructure(@Valid @RequestBody CreateFeeStructureDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feeService.createFeeStructure(dto));
    }

    @GetMapping("/fees/structures")
    @Operation(summary = "List all fee structures")
    public ResponseEntity<List<FeeStructure>> getAllFeeStructures() {
        return ResponseEntity.ok(feeService.getAllStructures());
    }

    @GetMapping("/fees/pending")
    @Operation(summary = "Get all pending fee payments")
    public ResponseEntity<List<FeePayment>> getPendingPayments() {
        return ResponseEntity.ok(feeService.getPendingPayments());
    }

    // --- Announcements ---

    @PostMapping("/announcements")
    @Operation(summary = "Create an announcement")
    public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody CreateAnnouncementDTO dto) {
        CustomUserDetails user = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementService.create(user.getId(), dto));
    }

    @GetMapping("/announcements")
    @Operation(summary = "List all announcements")
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAll());
    }

    // --- Dashboard (real counts) ---

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard overview")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        CustomUserDetails user = getCurrentUser();
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("adminEmail", user.getEmail());
        dashboard.put("role", user.getRole());
        dashboard.put("totalSubjects", subjectService.getAllSubjects().size());
        dashboard.put("totalCourses", courseService.getAll().size());
        dashboard.put("totalFeeStructures", feeService.getAllStructures().size());
        dashboard.put("pendingPayments", feeService.getPendingPayments().size());
        return ResponseEntity.ok(dashboard);
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) auth.getPrincipal();
    }
}
