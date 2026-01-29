package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "User profile endpoints (requires authentication)")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    @GetMapping
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<Map<String, Object>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", userDetails.getId());
        profile.put("email", userDetails.getEmail());
        profile.put("role", userDetails.getRole());
        profile.put("message", "Profile retrieved successfully");

        return ResponseEntity.ok(profile);
    }
}
