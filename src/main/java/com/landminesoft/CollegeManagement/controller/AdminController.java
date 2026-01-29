package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin-only endpoints (requires ADMIN role)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("adminId", userDetails.getId());
        dashboard.put("adminEmail", userDetails.getEmail());
        dashboard.put("role", userDetails.getRole());
        dashboard.put("accessTime", LocalDateTime.now().toString());
        dashboard.put("message", "Welcome to Admin Dashboard");

        // Add some sample dashboard stats
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalStudents", 150);
        stats.put("totalFaculty", 25);
        stats.put("activeCourses", 45);
        stats.put("pendingFeePayments", 12);
        dashboard.put("statistics", stats);

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/users")
    @Operation(summary = "List all users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> listUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User list retrieved successfully");
        response.put("note", "This endpoint demonstrates role-based access control");
        return ResponseEntity.ok(response);
    }
}
