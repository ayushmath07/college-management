package com.landminesoft.CollegeManagement.repository;

import com.landminesoft.CollegeManagement.entity.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {
    List<Marks> findByStudent_Id(Long studentId);

    List<Marks> findByStudent_IdAndSemester(Long studentId, Integer semester);

    List<Marks> findByCourse_Id(Long courseId);

    Optional<Marks> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
}
