package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.dto.UpdateFacultyProfileDTO;
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
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
@Tag(name = "Faculty", description = "Faculty profile management (requires FACULTY role)")
@SecurityRequirement(name = "bearerAuth")
public class FacultyController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    @Operation(summary = "Get current faculty's profile")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<Map<String, Object>> getProfile() {
        CustomUserDetails userDetails = getCurrentUser();
        Map<String, Object> profile = profileService.getFacultyProfile(userDetails.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current faculty's profile")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @Valid @RequestBody UpdateFacultyProfileDTO dto) {
        CustomUserDetails userDetails = getCurrentUser();
        Map<String, Object> response = profileService.updateFacultyProfile(userDetails.getId(), dto);
        return ResponseEntity.ok(response);
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
