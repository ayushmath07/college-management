package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.CreateSubjectDTO;
import com.landminesoft.CollegeManagement.entity.Subject;
import com.landminesoft.CollegeManagement.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional
    public Subject createSubject(CreateSubjectDTO dto) {
        subjectRepository.findBySubjectCode(dto.getSubjectCode()).ifPresent(s -> {
            throw new RuntimeException("Subject with code " + dto.getSubjectCode() + " already exists");
        });

        Subject subject = Subject.builder()
                .subjectCode(dto.getSubjectCode())
                .subjectName(dto.getSubjectName())
                .branch(dto.getBranch())
                .semester(dto.getSemester())
                .credits(dto.getCredits())
                .theoryMarks(dto.getTheoryMarks() != null ? dto.getTheoryMarks() : 100)
                .practicalMarks(dto.getPracticalMarks() != null ? dto.getPracticalMarks() : 50)
                .build();

        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
    }

    public List<Subject> getByBranchAndSemester(String branch, Integer semester) {
        return subjectRepository.findByBranchAndSemester(branch, semester);
    }
}
