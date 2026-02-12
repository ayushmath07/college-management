package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.dto.EnterMarksDTO;
import com.landminesoft.CollegeManagement.dto.MarkAttendanceDTO;
import com.landminesoft.CollegeManagement.dto.UpdateFacultyProfileDTO;
import com.landminesoft.CollegeManagement.entity.Attendance;
import com.landminesoft.CollegeManagement.entity.Course;
import com.landminesoft.CollegeManagement.entity.Enrollment;
import com.landminesoft.CollegeManagement.entity.Marks;
import com.landminesoft.CollegeManagement.security.CustomUserDetails;
import com.landminesoft.CollegeManagement.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
@Tag(name = "Faculty", description = "Faculty endpoints (requires FACULTY role)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('FACULTY')")
public class FacultyController {

    private final ProfileService profileService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;

    @GetMapping("/profile")
    @Operation(summary = "Get current faculty profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        return ResponseEntity.ok(profileService.getFacultyProfile(getCurrentUser().getId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update faculty profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@Valid @RequestBody UpdateFacultyProfileDTO dto) {
        return ResponseEntity.ok(profileService.updateFacultyProfile(getCurrentUser().getId(), dto));
    }

    @GetMapping("/courses")
    @Operation(summary = "Get courses assigned to this faculty")
    public ResponseEntity<List<Course>> getMyCourses() {
        return ResponseEntity.ok(courseService.getByFaculty(getCurrentUser().getId()));
    }

    @GetMapping("/courses/{courseId}/students")
    @Operation(summary = "Get students enrolled in a course")
    public ResponseEntity<List<Enrollment>> getCourseStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @PostMapping("/attendance")
    @Operation(summary = "Mark attendance for a class")
    public ResponseEntity<List<Attendance>> markAttendance(@Valid @RequestBody MarkAttendanceDTO dto) {
        List<Attendance> records = attendanceService.markAttendance(getCurrentUser().getId(), dto);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/marks")
    @Operation(summary = "Enter or update marks for a student")
    public ResponseEntity<Marks> enterMarks(@Valid @RequestBody EnterMarksDTO dto) {
        Marks marks = marksService.enterOrUpdateMarks(getCurrentUser().getId(), dto);
        return ResponseEntity.ok(marks);
    }

    @GetMapping("/courses/{courseId}/marks")
    @Operation(summary = "Get all marks for a course")
    public ResponseEntity<List<Marks>> getCourseMarks(@PathVariable Long courseId) {
        return ResponseEntity.ok(marksService.getByCourse(courseId));
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) auth.getPrincipal();
    }
}
