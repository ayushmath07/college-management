package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.MarkAttendanceDTO;
import com.landminesoft.CollegeManagement.entity.*;
import com.landminesoft.CollegeManagement.repository.AttendanceRepository;
import com.landminesoft.CollegeManagement.repository.CourseRepository;
import com.landminesoft.CollegeManagement.repository.FacultyRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Course testCourse;
    private FacultyPersonal testFaculty;

    @BeforeEach
    void setup() {
        testFaculty = FacultyPersonal.builder().id(1L).name("Dr. Smith").build();
        Subject sub = Subject.builder().id(1L).subjectCode("CS101").subjectName("Data Structures").build();
        testCourse = Course.builder().id(1L).faculty(testFaculty).subject(sub).semester(3).build();
    }

    @Test
    void testMarkAttendance() {
        MarkAttendanceDTO dto = new MarkAttendanceDTO();
        dto.setCourseId(1L);
        dto.setClassDate(LocalDate.of(2025, 1, 15));

        // create two entries
        MarkAttendanceDTO.AttendanceEntry e1 = new MarkAttendanceDTO.AttendanceEntry();
        e1.setStudentId(1L);
        e1.setStatus("PRESENT");
        MarkAttendanceDTO.AttendanceEntry e2 = new MarkAttendanceDTO.AttendanceEntry();
        e2.setStudentId(2L);
        e2.setStatus("ABSENT");
        dto.setRecords(Arrays.asList(e1, e2));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(testFaculty));
        when(studentRepository.findById(1L)).thenReturn(
                Optional.of(Student.builder().id(1L).name("Student 1").build()));
        when(studentRepository.findById(2L)).thenReturn(
                Optional.of(Student.builder().id(2L).name("Student 2").build()));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(inv -> {
            Attendance a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        List<Attendance> result = attendanceService.markAttendance(1L, dto);

        assertEquals(2, result.size());
        verify(attendanceRepository, times(2)).save(any(Attendance.class));
    }

    @Test
    void markAttendance_shouldFailForWrongFaculty() {
        // course is assigned to faculty 2, but faculty 1 tries to mark attendance
        Course otherFacultyCourse = Course.builder()
                .id(1L)
                .faculty(FacultyPersonal.builder().id(2L).build())
                .build();

        MarkAttendanceDTO dto = new MarkAttendanceDTO();
        dto.setCourseId(1L);
        dto.setClassDate(LocalDate.of(2025, 1, 15));
        dto.setRecords(List.of());

        when(courseRepository.findById(1L)).thenReturn(Optional.of(otherFacultyCourse));

        assertThrows(RuntimeException.class, () -> attendanceService.markAttendance(1L, dto));
    }

    @Test
    void testAttendanceSummaryCalculation() {
        // 3 records: 2 present, 1 absent => 66.67%
        Attendance a1 = Attendance.builder().id(1L).course(testCourse).status("PRESENT").build();
        Attendance a2 = Attendance.builder().id(2L).course(testCourse).status("ABSENT").build();
        Attendance a3 = Attendance.builder().id(3L).course(testCourse).status("PRESENT").build();

        when(attendanceRepository.findByStudent_Id(1L)).thenReturn(Arrays.asList(a1, a2, a3));

        List<Map<String, Object>> summary = attendanceService.getAttendanceSummary(1L);
        assertEquals(1, summary.size());

        Map<String, Object> row = summary.get(0);
        assertEquals(3, row.get("totalClasses"));
        assertEquals(2L, row.get("attended"));
        double pct = (Double) row.get("percentage");
        assertTrue(pct > 66 && pct < 67);
    }

    @Test
    void testGetByStudentAndCourse() {
        when(attendanceRepository.findByStudent_IdAndCourse_Id(1L, 1L))
                .thenReturn(List.of(Attendance.builder().id(1L).build()));

        assertEquals(1, attendanceService.getByStudentAndCourse(1L, 1L).size());
    }

    @Test
    void testGetByStudent() {
        when(attendanceRepository.findByStudent_Id(1L))
                .thenReturn(Arrays.asList(
                        Attendance.builder().id(1L).build(),
                        Attendance.builder().id(2L).build()));

        List<Attendance> result = attendanceService.getByStudent(1L);
        assertEquals(2, result.size());
    }
}
