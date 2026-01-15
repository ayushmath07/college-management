package com.landminesoft.CollegeManagement.repository;

import com.landminesoft.CollegeManagement.entity.FacultyPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<FacultyPersonal, Long> {
    Optional<FacultyPersonal> findByEmail(String email);
}
