package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.CreateFeeStructureDTO;
import com.landminesoft.CollegeManagement.dto.MakeFeePaymentDTO;
import com.landminesoft.CollegeManagement.entity.FeePayment;
import com.landminesoft.CollegeManagement.entity.FeeStructure;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.repository.FeePaymentRepository;
import com.landminesoft.CollegeManagement.repository.FeeStructureRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    @Mock
    private FeeStructureRepository feeStructureRepository;

    @Mock
    private FeePaymentRepository feePaymentRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private FeeService feeService;

    @Test
    void testCreateFeeStructure() {
        CreateFeeStructureDTO dto = new CreateFeeStructureDTO();
        dto.setBranch("CSE");
        dto.setSemester(3);
        dto.setTuitionFee(new BigDecimal("50000"));
        dto.setHostelFee(new BigDecimal("20000"));
        dto.setLibraryFee(new BigDecimal("2000"));
        dto.setLabFee(new BigDecimal("5000"));
        dto.setDueDate(LocalDate.of(2025, 3, 1));

        when(feeStructureRepository.save(any(FeeStructure.class))).thenAnswer(inv -> {
            FeeStructure fs = inv.getArgument(0);
            fs.setId(1L);
            return fs;
        });

        FeeStructure result = feeService.createFeeStructure(dto);

        assertNotNull(result);
        assertEquals("CSE", result.getBranch());
        assertEquals(new BigDecimal("77000"), result.getTotalFee());
        verify(feeStructureRepository).save(any(FeeStructure.class));
    }

    @Test
    void testNullFeesDefaultToZero() {
        CreateFeeStructureDTO dto = new CreateFeeStructureDTO();
        dto.setBranch("CSE");
        dto.setSemester(3);
        dto.setTuitionFee(null);
        dto.setHostelFee(null);
        dto.setLibraryFee(null);
        dto.setLabFee(null);

        when(feeStructureRepository.save(any(FeeStructure.class))).thenAnswer(inv -> inv.getArgument(0));

        FeeStructure result = feeService.createFeeStructure(dto);

        assertEquals(BigDecimal.ZERO, result.getTotalFee());
    }

    // tests for fee payment flow
    @Test
    void testMakePayment() {
        MakeFeePaymentDTO dto = new MakeFeePaymentDTO();
        dto.setFeeStructureId(1L);
        dto.setAmount(new BigDecimal("50000"));
        dto.setTransactionId("TXN-12345");

        Student student = Student.builder().id(1L).name("Rahul").build();
        FeeStructure fs = FeeStructure.builder().id(1L).branch("CSE").totalFee(new BigDecimal("77000")).build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeStructureRepository.findById(1L)).thenReturn(Optional.of(fs));
        when(feePaymentRepository.save(any(FeePayment.class))).thenAnswer(inv -> {
            FeePayment p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        FeePayment result = feeService.makePayment(1L, dto);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getPaymentStatus());
        assertEquals(new BigDecimal("50000"), result.getAmountPaid());
        assertEquals("TXN-12345", result.getTransactionId());
        assertTrue(result.getReceiptNumber().startsWith("RCP-"));
    }

    @Test
    void testPaymentStudentNotFound() {
        MakeFeePaymentDTO dto = new MakeFeePaymentDTO();
        dto.setFeeStructureId(1L);
        dto.setAmount(new BigDecimal("50000"));

        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> feeService.makePayment(1L, dto));
    }

    @Test
    void testPaymentFeeStructureNotFound() {
        MakeFeePaymentDTO dto = new MakeFeePaymentDTO();
        dto.setFeeStructureId(99L);
        dto.setAmount(new BigDecimal("50000"));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(Student.builder().id(1L).build()));
        when(feeStructureRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> feeService.makePayment(1L, dto));
    }

    @Test
    void testGetAllStructures() {
        when(feeStructureRepository.findAll()).thenReturn(Arrays.asList(
                FeeStructure.builder().id(1L).build(),
                FeeStructure.builder().id(2L).build()));

        assertEquals(2, feeService.getAllStructures().size());
    }

    @Test
    void testGetStudentPayments() {
        when(feePaymentRepository.findByStudent_Id(1L))
                .thenReturn(List.of(FeePayment.builder().id(1L).build()));

        assertEquals(1, feeService.getStudentPayments(1L).size());
    }

    @Test
    void testGetPendingPayments() {
        when(feePaymentRepository.findByPaymentStatus("PENDING"))
                .thenReturn(List.of(FeePayment.builder().id(1L).paymentStatus("PENDING").build()));

        assertEquals(1, feeService.getPendingPayments().size());
    }

    @Test
    void testGetByBranch() {
        when(feeStructureRepository.findByBranch("CSE"))
                .thenReturn(List.of(FeeStructure.builder().id(1L).branch("CSE").build()));

        assertEquals(1, feeService.getByBranch("CSE").size());
    }
}
