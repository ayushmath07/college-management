package com.landminesoft.CollegeManagement.security;

import com.landminesoft.CollegeManagement.entity.Admin;
import com.landminesoft.CollegeManagement.entity.FacultyPersonal;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.repository.AdminRepository;
import com.landminesoft.CollegeManagement.repository.FacultyRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find user in Student repository
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            Student s = student.get();
            return CustomUserDetails.builder()
                    .id(s.getId())
                    .email(s.getEmail())
                    .password(s.getPasswordHash())
                    .role("STUDENT")
                    .build();
        }

        // Try to find user in Faculty repository
        Optional<FacultyPersonal> faculty = facultyRepository.findByEmail(email);
        if (faculty.isPresent()) {
            FacultyPersonal f = faculty.get();
            return CustomUserDetails.builder()
                    .id(f.getId())
                    .email(f.getEmail())
                    .password(f.getPasswordHash())
                    .role("FACULTY")
                    .build();
        }

        // Try to find user in Admin repository
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            Admin a = admin.get();
            return CustomUserDetails.builder()
                    .id(a.getId())
                    .email(a.getEmail())
                    .password(a.getPasswordHash())
                    .role(a.getRole())
                    .build();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    /**
     * Load user details by email and role for JWT authentication.
     */
    public UserDetails loadUserByEmailAndRole(String email, String role) {
        return switch (role.toUpperCase()) {
            case "STUDENT" -> {
                Student s = studentRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Student not found: " + email));
                yield CustomUserDetails.builder()
                        .id(s.getId())
                        .email(s.getEmail())
                        .password(s.getPasswordHash())
                        .role("STUDENT")
                        .build();
            }
            case "FACULTY" -> {
                FacultyPersonal f = facultyRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Faculty not found: " + email));
                yield CustomUserDetails.builder()
                        .id(f.getId())
                        .email(f.getEmail())
                        .password(f.getPasswordHash())
                        .role("FACULTY")
                        .build();
            }
            default -> {
                // For Admin roles (SuperAdmin, Admin, Accountant)
                Admin a = adminRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + email));
                yield CustomUserDetails.builder()
                        .id(a.getId())
                        .email(a.getEmail())
                        .password(a.getPasswordHash())
                        .role(a.getRole())
                        .build();
            }
        };
    }
}
