package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.EnrollStudentDTO;
import com.landminesoft.CollegeManagement.entity.Course;
import com.landminesoft.CollegeManagement.entity.Enrollment;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.entity.Subject;
import com.landminesoft.CollegeManagement.repository.CourseRepository;
import com.landminesoft.CollegeManagement.repository.EnrollmentRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Student sampleStudent() {
        return Student.builder().id(1L).name("Rahul").email("rahul@college.edu").rollNumber("CSE2024001").build();
    }

    private Subject sampleSubject() {
        return Subject.builder().id(1L).subjectCode("CS101").subjectName("Data Structures").build();
    }

    private Course sampleCourse() {
        return Course.builder().id(1L).subject(sampleSubject()).semester(3).academicYear("2024-25").build();
    }

    @Test
    void enrollStudent_success() {
        EnrollStudentDTO dto = new EnrollStudentDTO();
        dto.setCourseId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent()));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse()));
        when(enrollmentRepository.findByStudent_IdAndCourse_Id(1L, 1L)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> {
            Enrollment e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        Enrollment result = enrollmentService.enrollStudent(1L, dto);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(3, result.getSemester());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void enrollStudent_duplicate_throws() {
        EnrollStudentDTO dto = new EnrollStudentDTO();
        dto.setCourseId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent()));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse()));
        when(enrollmentRepository.findByStudent_IdAndCourse_Id(1L, 1L))
                .thenReturn(Optional.of(Enrollment.builder().id(1L).build()));

        assertThrows(RuntimeException.class, () -> enrollmentService.enrollStudent(1L, dto));
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollStudent_studentNotFound_throws() {
        EnrollStudentDTO dto = new EnrollStudentDTO();
        dto.setCourseId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> enrollmentService.enrollStudent(1L, dto));
    }

    @Test
    void enrollStudent_courseNotFound_throws() {
        EnrollStudentDTO dto = new EnrollStudentDTO();
        dto.setCourseId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent()));
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> enrollmentService.enrollStudent(1L, dto));
    }

    @Test
    void dropEnrollment_success() {
        Student student = sampleStudent();
        Enrollment enrollment = Enrollment.builder().id(1L).student(student).status("ACTIVE").build();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = enrollmentService.dropEnrollment(1L, 1L);

        assertEquals("DROPPED", result.getStatus());
    }

    @Test
    void dropEnrollment_wrongStudent_throws() {
        Student otherStudent = Student.builder().id(2L).build();
        Enrollment enrollment = Enrollment.builder().id(1L).student(otherStudent).status("ACTIVE").build();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        assertThrows(RuntimeException.class, () -> enrollmentService.dropEnrollment(1L, 1L));
    }

    @Test
    void getStudentEnrollments() {
        when(enrollmentRepository.findByStudent_Id(1L))
                .thenReturn(Arrays.asList(Enrollment.builder().id(1L).build()));

        assertEquals(1, enrollmentService.getStudentEnrollments(1L).size());
    }

    @Test
    void getActiveEnrollments() {
        when(enrollmentRepository.findByStudent_IdAndStatus(1L, "ACTIVE"))
                .thenReturn(List.of(Enrollment.builder().id(1L).status("ACTIVE").build()));

        assertEquals(1, enrollmentService.getActiveEnrollments(1L).size());
    }

    @Test
    void getEnrollmentsByCourse() {
        when(enrollmentRepository.findByCourse_Id(1L))
                .thenReturn(Arrays.asList(
                        Enrollment.builder().id(1L).build(),
                        Enrollment.builder().id(2L).build()));

        assertEquals(2, enrollmentService.getEnrollmentsByCourse(1L).size());
    }
}
