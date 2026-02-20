package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.CreateCourseDTO;
import com.landminesoft.CollegeManagement.entity.Course;
import com.landminesoft.CollegeManagement.entity.FacultyPersonal;
import com.landminesoft.CollegeManagement.entity.Subject;
import com.landminesoft.CollegeManagement.repository.CourseRepository;
import com.landminesoft.CollegeManagement.repository.FacultyRepository;
import com.landminesoft.CollegeManagement.repository.SubjectRepository;
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
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private CourseService courseService;

    private FacultyPersonal sampleFaculty() {
        return FacultyPersonal.builder().id(1L).name("Dr. Smith").email("smith@college.edu").build();
    }

    private Subject sampleSubject() {
        return Subject.builder().id(1L).subjectCode("CS101").subjectName("Data Structures").build();
    }

    private CreateCourseDTO buildDTO() {
        CreateCourseDTO dto = new CreateCourseDTO();
        dto.setFacultyId(1L);
        dto.setSubjectId(1L);
        dto.setSemester(3);
        dto.setSection("A");
        dto.setAcademicYear("2024-25");
        dto.setTotalClasses(40);
        return dto;
    }

    @Test
    void createCourse_success() {
        CreateCourseDTO dto = buildDTO();
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(sampleFaculty()));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(sampleSubject()));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            Course c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        Course result = courseService.createCourse(dto);

        assertNotNull(result);
        assertEquals(3, result.getSemester());
        assertEquals("A", result.getSection());
        assertEquals("2024-25", result.getAcademicYear());
        assertEquals(40, result.getTotalClasses());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_defaultTotalClasses() {
        CreateCourseDTO dto = buildDTO();
        dto.setTotalClasses(null);
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(sampleFaculty()));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(sampleSubject()));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Course result = courseService.createCourse(dto);

        assertEquals(0, result.getTotalClasses());
    }

    @Test
    void createCourse_facultyNotFound_throws() {
        CreateCourseDTO dto = buildDTO();
        when(facultyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.createCourse(dto));
    }

    @Test
    void createCourse_subjectNotFound_throws() {
        CreateCourseDTO dto = buildDTO();
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(sampleFaculty()));
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.createCourse(dto));
    }

    @Test
    void getByFaculty() {
        List<Course> courses = List.of(Course.builder().id(1L).build());
        when(courseRepository.findByFaculty_Id(1L)).thenReturn(courses);

        assertEquals(1, courseService.getByFaculty(1L).size());
    }

    @Test
    void getAll() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(
                Course.builder().id(1L).build(),
                Course.builder().id(2L).build()));

        assertEquals(2, courseService.getAll().size());
    }

    @Test
    void getById_found() {
        Course course = Course.builder().id(1L).section("A").build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertEquals("A", courseService.getById(1L).getSection());
    }

    @Test
    void getById_notFound_throws() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.getById(99L));
    }
}
