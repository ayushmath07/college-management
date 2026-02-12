package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.MarkAttendanceDTO;
import com.landminesoft.CollegeManagement.entity.Attendance;
import com.landminesoft.CollegeManagement.entity.Course;
import com.landminesoft.CollegeManagement.entity.FacultyPersonal;
import com.landminesoft.CollegeManagement.entity.Student;
import com.landminesoft.CollegeManagement.repository.AttendanceRepository;
import com.landminesoft.CollegeManagement.repository.CourseRepository;
import com.landminesoft.CollegeManagement.repository.FacultyRepository;
import com.landminesoft.CollegeManagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Transactional
    public List<Attendance> markAttendance(Long facultyId, MarkAttendanceDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // make sure this faculty actually owns the course
        if (!course.getFaculty().getId().equals(facultyId)) {
            throw new RuntimeException("You're not assigned to this course");
        }

        FacultyPersonal faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        List<Attendance> saved = new ArrayList<>();
        for (MarkAttendanceDTO.AttendanceEntry entry : dto.getRecords()) {
            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student " + entry.getStudentId() + " not found"));

            Attendance attendance = Attendance.builder()
                    .student(student)
                    .course(course)
                    .classDate(dto.getClassDate())
                    .status(entry.getStatus().toUpperCase())
                    .markedBy(faculty)
                    .build();

            saved.add(attendanceRepository.save(attendance));
        }
        return saved;
    }

    public List<Attendance> getByStudentAndCourse(Long studentId, Long courseId) {
        return attendanceRepository.findByStudent_IdAndCourse_Id(studentId, courseId);
    }

    public List<Attendance> getByStudent(Long studentId) {
        return attendanceRepository.findByStudent_Id(studentId);
    }

    // returns a summary with percentage per course
    public List<Map<String, Object>> getAttendanceSummary(Long studentId) {
        List<Attendance> allRecords = attendanceRepository.findByStudent_Id(studentId);

        // group by course
        Map<Long, List<Attendance>> byCourse = new LinkedHashMap<>();
        for (Attendance a : allRecords) {
            byCourse.computeIfAbsent(a.getCourse().getId(), k -> new ArrayList<>()).add(a);
        }

        List<Map<String, Object>> summary = new ArrayList<>();
        for (Map.Entry<Long, List<Attendance>> entry : byCourse.entrySet()) {
            List<Attendance> records = entry.getValue();
            Attendance first = records.get(0);
            long present = records.stream().filter(r -> "PRESENT".equals(r.getStatus())).count();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("courseId", first.getCourse().getId());
            row.put("subjectName", first.getCourse().getSubject().getSubjectName());
            row.put("totalClasses", records.size());
            row.put("attended", present);
            double pct = records.isEmpty() ? 0 : (present * 100.0 / records.size());
            row.put("percentage", Math.round(pct * 100.0) / 100.0);
            summary.add(row);
        }
        return summary;
    }
}
