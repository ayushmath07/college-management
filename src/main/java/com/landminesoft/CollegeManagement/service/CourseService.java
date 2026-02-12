package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.CreateCourseDTO;
import com.landminesoft.CollegeManagement.entity.Course;
import com.landminesoft.CollegeManagement.entity.FacultyPersonal;
import com.landminesoft.CollegeManagement.entity.Subject;
import com.landminesoft.CollegeManagement.repository.CourseRepository;
import com.landminesoft.CollegeManagement.repository.FacultyRepository;
import com.landminesoft.CollegeManagement.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public Course createCourse(CreateCourseDTO dto) {
        FacultyPersonal faculty = facultyRepository.findById(dto.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty not found with id " + dto.getFacultyId()));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id " + dto.getSubjectId()));

        Course course = Course.builder()
                .faculty(faculty)
                .subject(subject)
                .semester(dto.getSemester())
                .section(dto.getSection())
                .academicYear(dto.getAcademicYear())
                .totalClasses(dto.getTotalClasses() != null ? dto.getTotalClasses() : 0)
                .build();

        return courseRepository.save(course);
    }

    public List<Course> getByFaculty(Long facultyId) {
        return courseRepository.findByFaculty_Id(facultyId);
    }

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public Course getById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }
}
