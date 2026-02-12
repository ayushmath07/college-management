package com.landminesoft.CollegeManagement.repository;

import com.landminesoft.CollegeManagement.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCode(String subjectCode);

    List<Subject> findByBranchAndSemester(String branch, Integer semester);

    List<Subject> findByBranch(String branch);
}
