package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.UpdateFacultyProfileDTO;
import com.landminesoft.CollegeManagement.dto.UpdateStudentProfileDTO;
import com.landminesoft.CollegeManagement.entity.FacultyPersonal;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.repository.FacultyRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private ProfileService profileService;

    private Student sampleStudent() {
        return Student.builder()
                .id(1L)
                .rollNumber("CSE2024001")
                .name("Rahul Kumar")
                .email("rahul@college.edu")
                .phone("9876543210")
                .branch("CSE")
                .semester(3)
                .enrollmentYear(2024)
                .dob(LocalDate.of(2003, 5, 15))
                .address("123 Main Street")
                .city("Delhi")
                .pincode("110001")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private FacultyPersonal sampleFaculty() {
        return FacultyPersonal.builder()
                .id(1L)
                .name("Dr. Smith")
                .email("smith@college.edu")
                .phone("8765432109")
                .designation("Professor")
                .department("CSE")
                .qualification("PhD Computer Science")
                .experienceYears(15)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getStudentProfile_success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent()));

        Map<String, Object> profile = profileService.getStudentProfile(1L);

        assertEquals(1L, profile.get("id"));
        assertEquals("CSE2024001", profile.get("rollNumber"));
        assertEquals("Rahul Kumar", profile.get("name"));
        assertEquals("rahul@college.edu", profile.get("email"));
        assertEquals("CSE", profile.get("branch"));
        assertEquals("STUDENT", profile.get("role"));
    }

    @Test
    void getStudentProfile_notFound_throws() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> profileService.getStudentProfile(99L));
    }

    @Test
    void updateStudentProfile_partialUpdate() {
        Student student = sampleStudent();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateStudentProfileDTO dto = new UpdateStudentProfileDTO();
        dto.setPhone("9999999999");
        dto.setCity("Mumbai");
        // address, pincode, dob left null — should not change

        Map<String, Object> response = profileService.updateStudentProfile(1L, dto);

        assertEquals("Profile updated successfully", response.get("message"));
        assertEquals("9999999999", response.get("phone"));
        assertEquals("Mumbai", response.get("city"));
        // original address should still be there
        assertEquals("123 Main Street", student.getAddress());
    }

    @Test
    void getFacultyProfile_success() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(sampleFaculty()));

        Map<String, Object> profile = profileService.getFacultyProfile(1L);

        assertEquals(1L, profile.get("id"));
        assertEquals("Dr. Smith", profile.get("name"));
        assertEquals("Professor", profile.get("designation"));
        assertEquals("FACULTY", profile.get("role"));
    }

    @Test
    void getFacultyProfile_notFound_throws() {
        when(facultyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> profileService.getFacultyProfile(99L));
    }

    @Test
    void updateFacultyProfile_partialUpdate() {
        FacultyPersonal faculty = sampleFaculty();
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(FacultyPersonal.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateFacultyProfileDTO dto = new UpdateFacultyProfileDTO();
        dto.setPhone("1111111111");
        dto.setExperienceYears(20);
        // qualification left null

        Map<String, Object> response = profileService.updateFacultyProfile(1L, dto);

        assertEquals("Profile updated successfully", response.get("message"));
        assertEquals("1111111111", response.get("phone"));
        assertEquals(20, response.get("experienceYears"));
        // original qualification should remain
        assertEquals("PhD Computer Science", faculty.getQualification());
    }
}
