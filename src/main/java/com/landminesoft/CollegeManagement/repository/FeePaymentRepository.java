package com.landminesoft.CollegeManagement.repository;

import com.landminesoft.CollegeManagement.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    List<FeePayment> findByStudent_Id(Long studentId);

    List<FeePayment> findByPaymentStatus(String status);
}
