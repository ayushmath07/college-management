package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.*;
import com.landminesoft.CollegeManagement.entity.Admin;
import com.landminesoft.CollegeManagement.entity.FacultyPersonal;
import com.landminesoft.CollegeManagement.entity.PasswordResetToken;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.exception.EmailAlreadyExistsException;
import com.landminesoft.CollegeManagement.exception.InvalidCredentialsException;
import com.landminesoft.CollegeManagement.exception.InvalidTokenException;
import com.landminesoft.CollegeManagement.repository.AdminRepository;
import com.landminesoft.CollegeManagement.repository.FacultyRepository;
import com.landminesoft.CollegeManagement.repository.PasswordResetTokenRepository;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
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

        String token = jwtUtils.generateToken(student.getId(), student.getEmail(), "STUDENT");

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

        String token = jwtUtils.generateToken(faculty.getId(), faculty.getEmail(), "FACULTY");

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

        String token = jwtUtils.generateToken(admin.getId(), admin.getEmail(), admin.getRole());

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

    // ==================== PASSWORD RESET ====================

    @Transactional
    public Map<String, Object> forgotPassword(ForgotPasswordDTO dto) {
        String email = dto.getEmail();
        String userType = null;

        // Check which type of user exists with this email
        if (studentRepository.findByEmail(email).isPresent()) {
            userType = "STUDENT";
        } else if (facultyRepository.findByEmail(email).isPresent()) {
            userType = "FACULTY";
        } else if (adminRepository.findByEmail(email).isPresent()) {
            userType = "ADMIN";
        }

        Map<String, Object> response = new HashMap<>();

        if (userType != null) {
            // Generate reset token
            String token = java.util.UUID.randomUUID().toString();

            // Save token to database (30 minutes expiry)
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .email(email)
                    .userType(userType)
                    .expiryDate(java.time.LocalDateTime.now().plusMinutes(30))
                    .build();

            passwordResetTokenRepository.save(resetToken);

            // Simulate sending email (log to console)
            System.out.println("===========================================");
            System.out.println("PASSWORD RESET EMAIL SIMULATION");
            System.out.println("To: " + email);
            System.out.println("Reset Token: " + token);
            System.out.println("Reset Link: http://localhost:8080/reset-password?token=" + token);
            System.out.println("Expires in: 30 minutes");
            System.out.println("===========================================");
        }

        // Always return success for security (don't reveal if email exists)
        response.put("message", "If the email exists, a reset link has been sent");
        return response;
    }

    @Transactional
    public Map<String, Object> resetPassword(ResetPasswordDTO dto) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (!resetToken.isValid()) {
            throw new InvalidTokenException("Token has expired or already been used");
        }

        // Find and update user password based on user type
        String newPasswordHash = passwordEncoder.encode(dto.getNewPassword());

        switch (resetToken.getUserType()) {
            case "STUDENT" -> {
                Student student = studentRepository.findByEmail(resetToken.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                student.setPasswordHash(newPasswordHash);
                studentRepository.save(student);
            }
            case "FACULTY" -> {
                FacultyPersonal faculty = facultyRepository.findByEmail(resetToken.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                faculty.setPasswordHash(newPasswordHash);
                facultyRepository.save(faculty);
            }
            case "ADMIN" -> {
                Admin admin = adminRepository.findByEmail(resetToken.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                admin.setPasswordHash(newPasswordHash);
                adminRepository.save(admin);
            }
        }

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password has been reset successfully");
        return response;
    }

    @Transactional
    public Map<String, Object> changePassword(Long userId, String userType, ChangePasswordDTO dto) {
        String currentPasswordHash;

        switch (userType) {
            case "STUDENT" -> {
                Student student = studentRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                currentPasswordHash = student.getPasswordHash();

                if (!passwordEncoder.matches(dto.getOldPassword(), currentPasswordHash)) {
                    throw new InvalidCredentialsException("Current password is incorrect");
                }

                student.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
                studentRepository.save(student);
            }
            case "FACULTY" -> {
                FacultyPersonal faculty = facultyRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                currentPasswordHash = faculty.getPasswordHash();

                if (!passwordEncoder.matches(dto.getOldPassword(), currentPasswordHash)) {
                    throw new InvalidCredentialsException("Current password is incorrect");
                }

                faculty.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
                facultyRepository.save(faculty);
            }
            default -> {
                Admin admin = adminRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                currentPasswordHash = admin.getPasswordHash();

                if (!passwordEncoder.matches(dto.getOldPassword(), currentPasswordHash)) {
                    throw new InvalidCredentialsException("Current password is incorrect");
                }

                admin.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
                adminRepository.save(admin);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return response;
    }
}
