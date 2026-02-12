package com.landminesoft.CollegeManagement.repository;

import com.landminesoft.CollegeManagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByFaculty_Id(Long facultyId);

    List<Course> findBySubject_Id(Long subjectId);

    List<Course> findBySemesterAndAcademicYear(Integer semester, String academicYear);
}
