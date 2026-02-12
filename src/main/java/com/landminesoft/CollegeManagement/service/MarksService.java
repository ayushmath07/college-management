package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.EnterMarksDTO;
import com.landminesoft.CollegeManagement.entity.Course;
import com.landminesoft.CollegeManagement.entity.Marks;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.repository.CourseRepository;
import com.landminesoft.CollegeManagement.repository.MarksRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarksService {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Marks enterOrUpdateMarks(Long facultyId, EnterMarksDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getFaculty().getId().equals(facultyId)) {
            throw new RuntimeException("You're not assigned to this course");
        }

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // update if marks already exist for this student+course
        Marks marks = marksRepository.findByStudent_IdAndCourse_Id(dto.getStudentId(), dto.getCourseId())
                .orElse(Marks.builder()
                        .student(student)
                        .subject(course.getSubject())
                        .course(course)
                        .semester(course.getSemester())
                        .academicYear(course.getAcademicYear())
                        .build());

        int theory = dto.getTheoryMarks() != null ? dto.getTheoryMarks() : 0;
        int practical = dto.getPracticalMarks() != null ? dto.getPracticalMarks() : 0;
        marks.setTheoryMarks(theory);
        marks.setPracticalMarks(practical);
        marks.setTotalMarks(theory + practical);
        if (dto.getGrade() != null) {
            marks.setGrade(dto.getGrade());
        }

        return marksRepository.save(marks);
    }

    public List<Marks> getByStudent(Long studentId) {
        return marksRepository.findByStudent_Id(studentId);
    }

    public List<Marks> getByStudentAndSemester(Long studentId, Integer semester) {
        return marksRepository.findByStudent_IdAndSemester(studentId, semester);
    }

    public List<Marks> getByCourse(Long courseId) {
        return marksRepository.findByCourse_Id(courseId);
    }
}
