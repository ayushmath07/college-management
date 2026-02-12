package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.EnrollStudentDTO;
import com.landminesoft.CollegeManagement.entity.Course;
import com.landminesoft.CollegeManagement.entity.Enrollment;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.repository.CourseRepository;
import com.landminesoft.CollegeManagement.repository.EnrollmentRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Enrollment enrollStudent(Long studentId, EnrollStudentDTO dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // don't allow duplicate enrollments
        enrollmentRepository.findByStudent_IdAndCourse_Id(studentId, dto.getCourseId())
                .ifPresent(e -> {
                    throw new RuntimeException("Already enrolled in this course");
                });

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .subject(course.getSubject())
                .course(course)
                .semester(course.getSemester())
                .academicYear(course.getAcademicYear())
                .status("ACTIVE")
                .enrolledDate(LocalDateTime.now())
                .build();

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment dropEnrollment(Long studentId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!enrollment.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("This enrollment doesn't belong to you");
        }

        enrollment.setStatus("DROPPED");
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudent_Id(studentId);
    }

    public List<Enrollment> getActiveEnrollments(Long studentId) {
        return enrollmentRepository.findByStudent_IdAndStatus(studentId, "ACTIVE");
    }

    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourse_Id(courseId);
    }
}
