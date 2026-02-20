package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.EnterMarksDTO;
import com.landminesoft.CollegeManagement.entity.*;
import com.landminesoft.CollegeManagement.repository.CourseRepository;
import com.landminesoft.CollegeManagement.repository.MarksRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
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
class MarksServiceTest {

    @Mock
    private MarksRepository marksRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private MarksService marksService;

    private Student student;
    private Course course;
    private Subject subject;

    @BeforeEach
    void init() {
        subject = Subject.builder().id(1L).subjectCode("CS101").build();
        FacultyPersonal faculty = FacultyPersonal.builder().id(1L).name("Dr. Smith").build();
        course = Course.builder().id(1L).faculty(faculty).subject(subject)
                .semester(3).academicYear("2024-25").build();
        student = Student.builder().id(1L).name("Rahul").build();
    }

    @Test
    void testEnterNewMarks() {
        EnterMarksDTO dto = new EnterMarksDTO();
        dto.setCourseId(1L);
        dto.setStudentId(1L);
        dto.setTheoryMarks(85);
        dto.setPracticalMarks(40);
        dto.setGrade("A");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(marksRepository.findByStudent_IdAndCourse_Id(1L, 1L)).thenReturn(Optional.empty());
        when(marksRepository.save(any(Marks.class))).thenAnswer(inv -> {
            Marks m = inv.getArgument(0);
            m.setId(1L);
            return m;
        });

        Marks result = marksService.enterOrUpdateMarks(1L, dto);

        assertNotNull(result);
        assertEquals(85, result.getTheoryMarks());
        assertEquals(40, result.getPracticalMarks());
        assertEquals(125, result.getTotalMarks()); // 85 + 40
        assertEquals("A", result.getGrade());
        verify(marksRepository).save(any(Marks.class));
    }

    @Test
    void testUpdateExistingMarks() {
        EnterMarksDTO dto = new EnterMarksDTO();
        dto.setCourseId(1L);
        dto.setStudentId(1L);
        dto.setTheoryMarks(85);
        dto.setPracticalMarks(40);
        dto.setGrade("A");

        // existing record with old marks
        Marks existing = Marks.builder()
                .id(1L).student(student).subject(subject).course(course)
                .theoryMarks(60).practicalMarks(30).totalMarks(90)
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(marksRepository.findByStudent_IdAndCourse_Id(1L, 1L)).thenReturn(Optional.of(existing));
        when(marksRepository.save(any(Marks.class))).thenAnswer(inv -> inv.getArgument(0));

        Marks result = marksService.enterOrUpdateMarks(1L, dto);

        assertEquals(85, result.getTheoryMarks());
        assertEquals(40, result.getPracticalMarks());
        assertEquals(125, result.getTotalMarks());
    }

    @Test
    void enterMarksWithWrongFacultyShouldThrow() {
        EnterMarksDTO dto = new EnterMarksDTO();
        dto.setCourseId(1L);
        dto.setStudentId(1L);
        dto.setTheoryMarks(85);
        dto.setPracticalMarks(40);
        dto.setGrade("A");

        // course belongs to faculty 2 but we're logged in as faculty 1
        FacultyPersonal otherFaculty = FacultyPersonal.builder().id(2L).build();
        Course otherCourse = Course.builder().id(1L).faculty(otherFaculty).subject(subject).build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(otherCourse));

        assertThrows(RuntimeException.class, () -> marksService.enterOrUpdateMarks(1L, dto));
    }

    @Test
    void testNullMarksDefaultToZero() {
        EnterMarksDTO dto = new EnterMarksDTO();
        dto.setCourseId(1L);
        dto.setStudentId(1L);
        // leaving marks null on purpose
        dto.setTheoryMarks(null);
        dto.setPracticalMarks(null);
        dto.setGrade(null);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(marksRepository.findByStudent_IdAndCourse_Id(1L, 1L)).thenReturn(Optional.empty());
        when(marksRepository.save(any(Marks.class))).thenAnswer(inv -> inv.getArgument(0));

        Marks result = marksService.enterOrUpdateMarks(1L, dto);

        assertEquals(0, result.getTheoryMarks());
        assertEquals(0, result.getPracticalMarks());
        assertEquals(0, result.getTotalMarks());
    }

    @Test
    void testGetMarksByStudent() {
        when(marksRepository.findByStudent_Id(1L))
                .thenReturn(Arrays.asList(Marks.builder().id(1L).build()));
        assertEquals(1, marksService.getByStudent(1L).size());
    }

    @Test
    void testGetBySemester() {
        when(marksRepository.findByStudent_IdAndSemester(1L, 3))
                .thenReturn(List.of(Marks.builder().id(1L).semester(3).build()));
        assertEquals(1, marksService.getByStudentAndSemester(1L, 3).size());
    }

    @Test
    void testGetByCourse() {
        when(marksRepository.findByCourse_Id(1L))
                .thenReturn(Arrays.asList(Marks.builder().id(1L).build(), Marks.builder().id(2L).build()));
        assertEquals(2, marksService.getByCourse(1L).size());
    }
}
