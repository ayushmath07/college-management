package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.dto.UpdateStudentProfileDTO;
import com.landminesoft.CollegeManagement.security.CustomUserDetails;
import com.landminesoft.CollegeManagement.service.ProfileService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Student profile management (requires STUDENT role)")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    @Operation(summary = "Get current student's profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> getProfile() {
        CustomUserDetails userDetails = getCurrentUser();
        Map<String, Object> profile = profileService.getStudentProfile(userDetails.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current student's profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @Valid @RequestBody UpdateStudentProfileDTO dto) {
        CustomUserDetails userDetails = getCurrentUser();
        Map<String, Object> response = profileService.updateStudentProfile(userDetails.getId(), dto);
        return ResponseEntity.ok(response);
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
