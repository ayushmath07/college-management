package com.landminesoft.CollegeManagement.repository;

import com.landminesoft.CollegeManagement.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent_Id(Long studentId);

    List<Enrollment> findByStudent_IdAndStatus(Long studentId, String status);

    List<Enrollment> findByCourse_Id(Long courseId);

    Optional<Enrollment> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
}
