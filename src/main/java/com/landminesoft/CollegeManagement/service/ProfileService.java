package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.UpdateFacultyProfileDTO;
import com.landminesoft.CollegeManagement.dto.UpdateStudentProfileDTO;
import com.landminesoft.CollegeManagement.entity.FacultyPersonal;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.repository.FacultyRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public Map<String, Object> getStudentProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", student.getId());
        profile.put("rollNumber", student.getRollNumber());
        profile.put("name", student.getName());
        profile.put("email", student.getEmail());
        profile.put("phone", student.getPhone());
        profile.put("branch", student.getBranch());
        profile.put("semester", student.getSemester());
        profile.put("enrollmentYear", student.getEnrollmentYear());
        profile.put("dob", student.getDob());
        profile.put("address", student.getAddress());
        profile.put("city", student.getCity());
        profile.put("pincode", student.getPincode());
        profile.put("createdAt", student.getCreatedAt());
        profile.put("role", "STUDENT");

        return profile;
    }

    @Transactional
    public Map<String, Object> updateStudentProfile(Long studentId, UpdateStudentProfileDTO dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // partial update
        if (dto.getPhone() != null) {
            student.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            student.setAddress(dto.getAddress());
        }
        if (dto.getCity() != null) {
            student.setCity(dto.getCity());
        }
        if (dto.getPincode() != null) {
            student.setPincode(dto.getPincode());
        }
        if (dto.getDob() != null) {
            student.setDob(dto.getDob());
        }

        studentRepository.save(student);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        response.put("id", student.getId());
        response.put("name", student.getName());
        response.put("email", student.getEmail());
        response.put("phone", student.getPhone());
        response.put("address", student.getAddress());
        response.put("city", student.getCity());
        response.put("pincode", student.getPincode());
        response.put("dob", student.getDob());

        return response;
    }

    public Map<String, Object> getFacultyProfile(Long facultyId) {
        FacultyPersonal faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", faculty.getId());
        profile.put("name", faculty.getName());
        profile.put("email", faculty.getEmail());
        profile.put("phone", faculty.getPhone());
        profile.put("designation", faculty.getDesignation());
        profile.put("department", faculty.getDepartment());
        profile.put("qualification", faculty.getQualification());
        profile.put("experienceYears", faculty.getExperienceYears());
        profile.put("createdAt", faculty.getCreatedAt());
        profile.put("role", "FACULTY");

        return profile;
    }

    @Transactional
    public Map<String, Object> updateFacultyProfile(Long facultyId, UpdateFacultyProfileDTO dto) {
        FacultyPersonal faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        if (dto.getPhone() != null) {
            faculty.setPhone(dto.getPhone());
        }
        if (dto.getQualification() != null) {
            faculty.setQualification(dto.getQualification());
        }
        if (dto.getExperienceYears() != null) {
            faculty.setExperienceYears(dto.getExperienceYears());
        }

        facultyRepository.save(faculty);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        response.put("id", faculty.getId());
        response.put("name", faculty.getName());
        response.put("email", faculty.getEmail());
        response.put("phone", faculty.getPhone());
        response.put("qualification", faculty.getQualification());
        response.put("experienceYears", faculty.getExperienceYears());

        return response;
    }
}
