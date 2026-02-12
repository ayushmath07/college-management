package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.dto.*;
import com.landminesoft.CollegeManagement.security.CustomUserDetails;
import com.landminesoft.CollegeManagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration and Login APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/student/register")
    @Operation(summary = "Register a new student")
    public ResponseEntity<Map<String, Object>> registerStudent(
            @Valid @RequestBody StudentRegisterDTO dto) {
        Map<String, Object> response = authService.registerStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/student/login")
    @Operation(summary = "Student login")
    public ResponseEntity<JwtResponseDTO> loginStudent(
            @Valid @RequestBody LoginDTO dto) {
        JwtResponseDTO response = authService.loginStudent(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/faculty/register")
    @Operation(summary = "Register a new faculty member")
    public ResponseEntity<Map<String, Object>> registerFaculty(
            @Valid @RequestBody FacultyRegisterDTO dto) {
        Map<String, Object> response = authService.registerFaculty(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/faculty/login")
    @Operation(summary = "Faculty login")
    public ResponseEntity<JwtResponseDTO> loginFaculty(
            @Valid @RequestBody LoginDTO dto) {
        JwtResponseDTO response = authService.loginFaculty(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/register")
    @Operation(summary = "Register a new admin")
    public ResponseEntity<Map<String, Object>> registerAdmin(
            @Valid @RequestBody AdminRegisterDTO dto) {
        Map<String, Object> response = authService.registerAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin/login")
    @Operation(summary = "Admin login")
    public ResponseEntity<JwtResponseDTO> loginAdmin(
            @Valid @RequestBody LoginDTO dto) {
        JwtResponseDTO response = authService.loginAdmin(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset link")
    public ResponseEntity<Map<String, Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordDTO dto) {
        Map<String, Object> response = authService.forgotPassword(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using token")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @Valid @RequestBody ResetPasswordDTO dto) {
        Map<String, Object> response = authService.resetPassword(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password (requires authentication)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> changePassword(
            @Valid @RequestBody ChangePasswordDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> response = authService.changePassword(
                userDetails.getId(),
                userDetails.getRole(),
                dto);
        return ResponseEntity.ok(response);
    }
}
