package com.landminesoft.CollegeManagement.repository;

import com.landminesoft.CollegeManagement.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByBranchAndSemester(String branch, Integer semester);

    List<FeeStructure> findByBranch(String branch);
}
