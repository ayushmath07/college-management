package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.CreateAnnouncementDTO;
import com.landminesoft.CollegeManagement.entity.Admin;
import com.landminesoft.CollegeManagement.entity.Announcement;
import com.landminesoft.CollegeManagement.repository.AdminRepository;
import com.landminesoft.CollegeManagement.repository.AnnouncementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;
    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AnnouncementService announcementService;

    private Admin sampleAdmin() {
        return Admin.builder().id(1L).name("Admin User").email("admin@college.edu").role("SuperAdmin").build();
    }

    @Test
    void testCreateAnnouncement() {
        CreateAnnouncementDTO dto = new CreateAnnouncementDTO();
        dto.setTitle("Exam Schedule Released");
        dto.setDescription("Mid-term exams start from March 1st");
        dto.setTargetAudience("student");
        dto.setExpiresAt(LocalDate.of(2025, 3, 15));

        when(adminRepository.findById(1L)).thenReturn(Optional.of(sampleAdmin()));
        when(announcementRepository.save(any(Announcement.class))).thenAnswer(inv -> {
            Announcement a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        Announcement result = announcementService.create(1L, dto);

        assertNotNull(result);
        assertEquals("Exam Schedule Released", result.getTitle());
        assertEquals("STUDENT", result.getTargetAudience()); // uppercased
        verify(announcementRepository).save(any(Announcement.class));
    }

    @Test
    void testCreateWithInvalidAdmin() {
        CreateAnnouncementDTO dto = new CreateAnnouncementDTO();
        dto.setTitle("Test");
        dto.setTargetAudience("ALL");

        when(adminRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> announcementService.create(99L, dto));
    }

    @Test
    void testGetAll() {
        when(announcementRepository.findAll()).thenReturn(Arrays.asList(
                Announcement.builder().id(1L).title("A1").build(),
                Announcement.builder().id(2L).title("A2").build()));

        assertEquals(2, announcementService.getAll().size());
    }

    @Test
    void testGetActiveAnnouncements() {
        Announcement active = Announcement.builder()
                .id(1L).title("Active").expiresAt(LocalDate.now().plusDays(5)).build();
        when(announcementRepository.findByExpiresAtAfterOrExpiresAtIsNull(any(LocalDate.class)))
                .thenReturn(List.of(active));

        List<Announcement> result = announcementService.getActive();

        assertEquals(1, result.size());
        assertEquals("Active", result.get(0).getTitle());
    }

    @Test
    void testFilterByAudience() {
        when(announcementRepository.findByTargetAudience("STUDENT"))
                .thenReturn(List.of(Announcement.builder().id(1L).targetAudience("STUDENT").build()));

        List<Announcement> result = announcementService.getByAudience("student");

        assertEquals(1, result.size());
    }
}
