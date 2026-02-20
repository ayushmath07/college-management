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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    // -- helper DTOs --

    private StudentRegisterDTO studentRegDTO() {
        StudentRegisterDTO dto = new StudentRegisterDTO();
        dto.setName("Rahul Kumar");
        dto.setEmail("rahul@college.edu");
        dto.setPhone("9876543210");
        dto.setPassword("SecurePass@123");
        dto.setBranch("CSE");
        dto.setEnrollmentYear(2024);
        return dto;
    }

    private FacultyRegisterDTO facultyRegDTO() {
        FacultyRegisterDTO dto = new FacultyRegisterDTO();
        dto.setName("Dr. Smith");
        dto.setEmail("smith@college.edu");
        dto.setPhone("8765432109");
        dto.setPassword("SecurePass@123");
        dto.setDepartment("CSE");
        dto.setDesignation("Professor");
        return dto;
    }

    private AdminRegisterDTO adminRegDTO() {
        AdminRegisterDTO dto = new AdminRegisterDTO();
        dto.setName("Admin User");
        dto.setEmail("admin@college.edu");
        dto.setPhone("7654321098");
        dto.setPassword("SecurePass@123");
        dto.setRole("SuperAdmin");
        return dto;
    }

    private LoginDTO loginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("rahul@college.edu");
        dto.setPassword("SecurePass@123");
        return dto;
    }

    // -- student registration tests --

    @Test
    void registerStudent_success() {
        StudentRegisterDTO dto = studentRegDTO();
        when(studentRepository.findByEmail("rahul@college.edu")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SecurePass@123")).thenReturn("hashed_password");
        when(studentRepository.count()).thenReturn(0L);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        Map<String, Object> result = authService.registerStudent(dto);

        assertEquals(1L, result.get("id"));
        assertEquals("Rahul Kumar", result.get("name"));
        assertEquals("rahul@college.edu", result.get("email"));
        assertEquals("STUDENT", result.get("role"));
        assertEquals("Registration successful. Please login.", result.get("message"));
        assertNotNull(result.get("rollNumber"));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void registerStudent_duplicateEmail_throws() {
        StudentRegisterDTO dto = studentRegDTO();
        when(studentRepository.findByEmail("rahul@college.edu"))
                .thenReturn(Optional.of(Student.builder().id(1L).build()));

        assertThrows(EmailAlreadyExistsException.class, () -> authService.registerStudent(dto));
        verify(studentRepository, never()).save(any());
    }

    // -- student login tests --

    @Test
    void loginStudent_success() {
        LoginDTO dto = loginDTO();
        Student student = Student.builder()
                .id(1L).email("rahul@college.edu").passwordHash("hashed").build();

        when(studentRepository.findByEmail("rahul@college.edu")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("SecurePass@123", "hashed")).thenReturn(true);
        when(jwtUtils.generateToken(1L, "rahul@college.edu", "STUDENT")).thenReturn("jwt_token");

        JwtResponseDTO result = authService.loginStudent(dto);

        assertEquals("jwt_token", result.getToken());
        assertEquals(1L, result.getUserId());
        assertEquals("STUDENT", result.getRole());
    }

    @Test
    void loginStudent_wrongEmail_throws() {
        LoginDTO dto = loginDTO();
        when(studentRepository.findByEmail("rahul@college.edu")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.loginStudent(dto));
    }

    @Test
    void loginStudent_wrongPassword_throws() {
        LoginDTO dto = loginDTO();
        Student student = Student.builder()
                .id(1L).email("rahul@college.edu").passwordHash("hashed").build();

        when(studentRepository.findByEmail("rahul@college.edu")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("SecurePass@123", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.loginStudent(dto));
    }

    // -- faculty registration & login --

    @Test
    void registerFaculty_success() {
        FacultyRegisterDTO dto = facultyRegDTO();
        when(facultyRepository.findByEmail("smith@college.edu")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SecurePass@123")).thenReturn("hashed_password");
        when(facultyRepository.save(any(FacultyPersonal.class))).thenAnswer(inv -> {
            FacultyPersonal f = inv.getArgument(0);
            f.setId(1L);
            return f;
        });

        Map<String, Object> result = authService.registerFaculty(dto);

        assertEquals("FACULTY", result.get("role"));
        assertEquals("Registration successful. Please login.", result.get("message"));
    }

    @Test
    void registerFaculty_duplicateEmail_throws() {
        FacultyRegisterDTO dto = facultyRegDTO();
        when(facultyRepository.findByEmail("smith@college.edu"))
                .thenReturn(Optional.of(FacultyPersonal.builder().id(1L).build()));

        assertThrows(EmailAlreadyExistsException.class, () -> authService.registerFaculty(dto));
    }

    @Test
    void loginFaculty_success() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("smith@college.edu");
        dto.setPassword("SecurePass@123");

        FacultyPersonal faculty = FacultyPersonal.builder()
                .id(1L).email("smith@college.edu").passwordHash("hashed").build();

        when(facultyRepository.findByEmail("smith@college.edu")).thenReturn(Optional.of(faculty));
        when(passwordEncoder.matches("SecurePass@123", "hashed")).thenReturn(true);
        when(jwtUtils.generateToken(1L, "smith@college.edu", "FACULTY")).thenReturn("faculty_token");

        JwtResponseDTO result = authService.loginFaculty(dto);

        assertEquals("faculty_token", result.getToken());
        assertEquals("FACULTY", result.getRole());
    }

    // -- admin registration & login --

    @Test
    void registerAdmin_success() {
        AdminRegisterDTO dto = adminRegDTO();
        when(adminRepository.findByEmail("admin@college.edu")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SecurePass@123")).thenReturn("hashed_password");
        when(adminRepository.save(any(Admin.class))).thenAnswer(inv -> {
            Admin a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        Map<String, Object> result = authService.registerAdmin(dto);

        assertEquals("SuperAdmin", result.get("role"));
        assertEquals("Registration successful. Please login.", result.get("message"));
    }

    @Test
    void registerAdmin_duplicateEmail_throws() {
        AdminRegisterDTO dto = adminRegDTO();
        when(adminRepository.findByEmail("admin@college.edu"))
                .thenReturn(Optional.of(Admin.builder().id(1L).build()));

        assertThrows(EmailAlreadyExistsException.class, () -> authService.registerAdmin(dto));
    }

    @Test
    void loginAdmin_success() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("admin@college.edu");
        dto.setPassword("SecurePass@123");

        Admin admin = Admin.builder()
                .id(1L).email("admin@college.edu").passwordHash("hashed").role("SuperAdmin").build();

        when(adminRepository.findByEmail("admin@college.edu")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("SecurePass@123", "hashed")).thenReturn(true);
        when(jwtUtils.generateToken(1L, "admin@college.edu", "SuperAdmin")).thenReturn("admin_token");

        JwtResponseDTO result = authService.loginAdmin(dto);

        assertEquals("admin_token", result.getToken());
        assertEquals("SuperAdmin", result.getRole());
    }

    // -- forgot password --

    @Test
    void forgotPassword_knownEmail_createsToken() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("rahul@college.edu");

        when(studentRepository.findByEmail("rahul@college.edu"))
                .thenReturn(Optional.of(Student.builder().id(1L).build()));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = authService.forgotPassword(dto);

        assertEquals("If the email exists, a reset link has been sent", result.get("message"));
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void forgotPassword_unknownEmail_noTokenCreated() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("nobody@college.edu");

        when(studentRepository.findByEmail("nobody@college.edu")).thenReturn(Optional.empty());
        when(facultyRepository.findByEmail("nobody@college.edu")).thenReturn(Optional.empty());
        when(adminRepository.findByEmail("nobody@college.edu")).thenReturn(Optional.empty());

        Map<String, Object> result = authService.forgotPassword(dto);

        // should still return success (no email leaking)
        assertEquals("If the email exists, a reset link has been sent", result.get("message"));
        verify(passwordResetTokenRepository, never()).save(any());
    }

    // -- reset password --

    @Test
    void resetPassword_validToken_student() {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("valid-token");
        dto.setNewPassword("NewPass@123");

        PasswordResetToken token = PasswordResetToken.builder()
                .id(1L).token("valid-token").email("rahul@college.edu")
                .userType("STUDENT").expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(false).build();

        Student student = Student.builder().id(1L).email("rahul@college.edu").passwordHash("old").build();

        when(passwordResetTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new_hashed");
        when(studentRepository.findByEmail("rahul@college.edu")).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = authService.resetPassword(dto);

        assertEquals("Password has been reset successfully", result.get("message"));
        assertEquals("new_hashed", student.getPasswordHash());
        assertTrue(token.getUsed());
    }

    @Test
    void resetPassword_invalidToken_throws() {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("bad-token");
        dto.setNewPassword("NewPass@123");

        when(passwordResetTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> authService.resetPassword(dto));
    }

    @Test
    void resetPassword_expiredToken_throws() {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("expired-token");
        dto.setNewPassword("NewPass@123");

        PasswordResetToken token = PasswordResetToken.builder()
                .id(1L).token("expired-token").email("rahul@college.edu")
                .userType("STUDENT").expiryDate(LocalDateTime.now().minusMinutes(10))
                .used(false).build();

        when(passwordResetTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> authService.resetPassword(dto));
    }

    // -- change password --

    @Test
    void changePassword_student_success() {
        ChangePasswordDTO dto = new ChangePasswordDTO("OldPass@123", "NewPass@123");

        Student student = Student.builder().id(1L).passwordHash("old_hashed").build();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("OldPass@123", "old_hashed")).thenReturn(true);
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new_hashed");
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = authService.changePassword(1L, "STUDENT", dto);

        assertEquals("Password changed successfully", result.get("message"));
        assertEquals("new_hashed", student.getPasswordHash());
    }

    @Test
    void changePassword_wrongOldPassword_throws() {
        ChangePasswordDTO dto = new ChangePasswordDTO("WrongPass@1", "NewPass@123");

        Student student = Student.builder().id(1L).passwordHash("old_hashed").build();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("WrongPass@1", "old_hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.changePassword(1L, "STUDENT", dto));
    }

    @Test
    void changePassword_faculty_success() {
        ChangePasswordDTO dto = new ChangePasswordDTO("OldPass@123", "NewPass@123");

        FacultyPersonal faculty = FacultyPersonal.builder().id(1L).passwordHash("old_hashed").build();
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(passwordEncoder.matches("OldPass@123", "old_hashed")).thenReturn(true);
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new_hashed");
        when(facultyRepository.save(any(FacultyPersonal.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = authService.changePassword(1L, "FACULTY", dto);

        assertEquals("Password changed successfully", result.get("message"));
    }

    @Test
    void changePassword_admin_success() {
        ChangePasswordDTO dto = new ChangePasswordDTO("OldPass@123", "NewPass@123");

        Admin admin = Admin.builder().id(1L).passwordHash("old_hashed").build();
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("OldPass@123", "old_hashed")).thenReturn(true);
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new_hashed");
        when(adminRepository.save(any(Admin.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = authService.changePassword(1L, "ADMIN", dto);

        assertEquals("Password changed successfully", result.get("message"));
    }
}
