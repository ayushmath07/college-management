package com.landminesoft.CollegeManagement.repository;

import com.landminesoft.CollegeManagement.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    List<Attendance> findByCourse_IdAndClassDate(Long courseId, LocalDate classDate);

    List<Attendance> findByStudent_Id(Long studentId);

    long countByStudent_IdAndCourse_IdAndStatus(Long studentId, Long courseId, String status);
}
