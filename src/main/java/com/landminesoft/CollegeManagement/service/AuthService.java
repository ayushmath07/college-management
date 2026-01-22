package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.*;
import com.landminesoft.CollegeManagement.entity.Admin;
import com.landminesoft.CollegeManagement.entity.FacultyPersonal;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.exception.EmailAlreadyExistsException;
import com.landminesoft.CollegeManagement.exception.InvalidCredentialsException;
import com.landminesoft.CollegeManagement.repository.AdminRepository;
import com.landminesoft.CollegeManagement.repository.FacultyRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import com.landminesoft.CollegeManagement.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // ==================== STUDENT ====================

    @Transactional
    public Map<String, Object> registerStudent(StudentRegisterDTO dto) {
        // Check if email already exists
        if (studentRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        // Generate roll number: BRANCH + YEAR + SEQUENCE
        String rollNumber = generateRollNumber(dto.getBranch(), dto.getEnrollmentYear());

        // Build student entity
        Student student = Student.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .branch(dto.getBranch())
                .enrollmentYear(dto.getEnrollmentYear())
                .rollNumber(rollNumber)
                .semester(1) // Default to first semester
                .build();

        Student saved = studentRepository.save(student);

        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("name", saved.getName());
        response.put("email", saved.getEmail());
        response.put("rollNumber", saved.getRollNumber());
        response.put("role", "STUDENT");
        response.put("message", "Registration successful. Please login.");
        return response;
    }

    public JwtResponseDTO loginStudent(LoginDTO dto) {
        Student student = studentRepository.findByEmail(dto.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.getPassword(), student.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtUtils.generateToken(student.getEmail(), "STUDENT");

        return JwtResponseDTO.builder()
                .token(token)
                .userId(student.getId())
                .email(student.getEmail())
                .role("STUDENT")
                .build();
    }

    // ==================== FACULTY ====================

    @Transactional
    public Map<String, Object> registerFaculty(FacultyRegisterDTO dto) {
        if (facultyRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        FacultyPersonal faculty = FacultyPersonal.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .department(dto.getDepartment())
                .designation(dto.getDesignation())
                .build();

        FacultyPersonal saved = facultyRepository.save(faculty);

        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("name", saved.getName());
        response.put("email", saved.getEmail());
        response.put("role", "FACULTY");
        response.put("message", "Registration successful. Please login.");
        return response;
    }

    public JwtResponseDTO loginFaculty(LoginDTO dto) {
        FacultyPersonal faculty = facultyRepository.findByEmail(dto.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.getPassword(), faculty.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtUtils.generateToken(faculty.getEmail(), "FACULTY");

        return JwtResponseDTO.builder()
                .token(token)
                .userId(faculty.getId())
                .email(faculty.getEmail())
                .role("FACULTY")
                .build();
    }

    // ==================== ADMIN ====================

    @Transactional
    public Map<String, Object> registerAdmin(AdminRegisterDTO dto) {
        if (adminRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        Admin admin = Admin.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();

        Admin saved = adminRepository.save(admin);

        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("name", saved.getName());
        response.put("email", saved.getEmail());
        response.put("role", saved.getRole());
        response.put("message", "Registration successful. Please login.");
        return response;
    }

    public JwtResponseDTO loginAdmin(LoginDTO dto) {
        Admin admin = adminRepository.findByEmail(dto.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.getPassword(), admin.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtUtils.generateToken(admin.getEmail(), admin.getRole());

        return JwtResponseDTO.builder()
                .token(token)
                .userId(admin.getId())
                .email(admin.getEmail())
                .role(admin.getRole())
                .build();
    }

    // ==================== HELPERS ====================

    private String generateRollNumber(String branch, Integer enrollmentYear) {
        // Format: BRANCH + YEAR + SEQUENCE (e.g., CSE2024001)
        long count = studentRepository.count() + 1;
        return String.format("%s%d%03d", branch.toUpperCase(), enrollmentYear, count);
    }
}
