package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.CreateSubjectDTO;
import com.landminesoft.CollegeManagement.entity.Subject;
import com.landminesoft.CollegeManagement.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    @Test
    void testCreateSubject() {
        // setup dto manually here
        CreateSubjectDTO dto = new CreateSubjectDTO();
        dto.setSubjectCode("CS101");
        dto.setSubjectName("Data Structures");
        dto.setBranch("CSE");
        dto.setSemester(3);
        dto.setCredits(4);
        dto.setTheoryMarks(100);
        dto.setPracticalMarks(50);

        when(subjectRepository.findBySubjectCode("CS101")).thenReturn(Optional.empty());
        when(subjectRepository.save(any(Subject.class))).thenAnswer(inv -> {
            Subject s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        Subject result = subjectService.createSubject(dto);

        assertNotNull(result);
        assertEquals("CS101", result.getSubjectCode());
        assertEquals("Data Structures", result.getSubjectName());
        assertEquals("CSE", result.getBranch());
        assertEquals(3, result.getSemester());
        assertEquals(4, result.getCredits());
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    void testCreateSubjectDuplicateCode() {
        CreateSubjectDTO dto = new CreateSubjectDTO();
        dto.setSubjectCode("CS101");
        dto.setSubjectName("Data Structures");
        dto.setBranch("CSE");
        dto.setSemester(3);
        dto.setCredits(4);

        when(subjectRepository.findBySubjectCode("CS101"))
                .thenReturn(Optional.of(Subject.builder().subjectCode("CS101").build()));

        assertThrows(RuntimeException.class, () -> subjectService.createSubject(dto));
        verify(subjectRepository, never()).save(any());
    }

    @Test
    void testDefaultMarksWhenNull() {
        CreateSubjectDTO dto = new CreateSubjectDTO();
        dto.setSubjectCode("CS101");
        dto.setSubjectName("Data Structures");
        dto.setBranch("CSE");
        dto.setSemester(3);
        dto.setCredits(4);
        dto.setTheoryMarks(null);
        dto.setPracticalMarks(null);

        when(subjectRepository.findBySubjectCode("CS101")).thenReturn(Optional.empty());
        when(subjectRepository.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));

        Subject result = subjectService.createSubject(dto);
        // should default to 100 and 50
        assertEquals(100, result.getTheoryMarks());
        assertEquals(50, result.getPracticalMarks());
    }

    @Test
    void testGetAllSubjects() {
        List<Subject> subjects = Arrays.asList(
                Subject.builder().id(1L).subjectCode("CS101").build(),
                Subject.builder().id(2L).subjectCode("CS102").build());
        when(subjectRepository.findAll()).thenReturn(subjects);

        List<Subject> result = subjectService.getAllSubjects();

        assertEquals(2, result.size());
    }

    @Test
    void testGetByIdFound() {
        Subject subject = Subject.builder().id(1L).subjectCode("CS101").build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        Subject result = subjectService.getById(1L);
        assertEquals("CS101", result.getSubjectCode());
    }

    @Test
    void testGetByIdNotFound() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> subjectService.getById(99L));
    }

    @Test
    void testFilterByBranchAndSemester() {
        List<Subject> subjects = List.of(Subject.builder().id(1L).branch("CSE").semester(3).build());
        when(subjectRepository.findByBranchAndSemester("CSE", 3)).thenReturn(subjects);

        List<Subject> result = subjectService.getByBranchAndSemester("CSE", 3);
        assertEquals(1, result.size());
    }
}
