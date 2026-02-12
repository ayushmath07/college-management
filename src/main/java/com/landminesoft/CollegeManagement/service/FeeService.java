package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.CreateFeeStructureDTO;
import com.landminesoft.CollegeManagement.dto.MakeFeePaymentDTO;
import com.landminesoft.CollegeManagement.entity.FeePayment;
import com.landminesoft.CollegeManagement.entity.FeeStructure;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.repository.FeePaymentRepository;
import com.landminesoft.CollegeManagement.repository.FeeStructureRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeStructureRepository feeStructureRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public FeeStructure createFeeStructure(CreateFeeStructureDTO dto) {
        BigDecimal tuition = dto.getTuitionFee() != null ? dto.getTuitionFee() : BigDecimal.ZERO;
        BigDecimal hostel = dto.getHostelFee() != null ? dto.getHostelFee() : BigDecimal.ZERO;
        BigDecimal library = dto.getLibraryFee() != null ? dto.getLibraryFee() : BigDecimal.ZERO;
        BigDecimal lab = dto.getLabFee() != null ? dto.getLabFee() : BigDecimal.ZERO;

        FeeStructure fs = FeeStructure.builder()
                .branch(dto.getBranch())
                .semester(dto.getSemester())
                .tuitionFee(tuition)
                .hostelFee(hostel)
                .libraryFee(library)
                .labFee(lab)
                .totalFee(tuition.add(hostel).add(library).add(lab))
                .dueDate(dto.getDueDate())
                .build();

        return feeStructureRepository.save(fs);
    }

    public List<FeeStructure> getAllStructures() {
        return feeStructureRepository.findAll();
    }

    public List<FeeStructure> getByBranch(String branch) {
        return feeStructureRepository.findByBranch(branch);
    }

    @Transactional
    public FeePayment makePayment(Long studentId, MakeFeePaymentDTO dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        FeeStructure fs = feeStructureRepository.findById(dto.getFeeStructureId())
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        String receipt = "RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        FeePayment payment = FeePayment.builder()
                .student(student)
                .feeStructure(fs)
                .amountPaid(dto.getAmount())
                .paymentDate(LocalDateTime.now())
                .transactionId(dto.getTransactionId() != null ? dto.getTransactionId() : UUID.randomUUID().toString())
                .paymentStatus("COMPLETED")
                .receiptNumber(receipt)
                .build();

        return feePaymentRepository.save(payment);
    }

    public List<FeePayment> getStudentPayments(Long studentId) {
        return feePaymentRepository.findByStudent_Id(studentId);
    }

    public List<FeePayment> getPendingPayments() {
        return feePaymentRepository.findByPaymentStatus("PENDING");
    }
}
