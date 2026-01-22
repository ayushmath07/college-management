package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.dto.*;
import com.landminesoft.CollegeManagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration and Login APIs")
public class AuthController {

    private final AuthService authService;

    // ==================== STUDENT ====================

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

    // ==================== FACULTY ====================

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

    // ==================== ADMIN ====================

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
}
