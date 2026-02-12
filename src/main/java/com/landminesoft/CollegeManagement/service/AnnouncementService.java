package com.landminesoft.CollegeManagement.service;

import com.landminesoft.CollegeManagement.dto.CreateAnnouncementDTO;
import com.landminesoft.CollegeManagement.entity.Admin;
import com.landminesoft.CollegeManagement.entity.Announcement;
import com.landminesoft.CollegeManagement.repository.AdminRepository;
import com.landminesoft.CollegeManagement.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public Announcement create(Long adminId, CreateAnnouncementDTO dto) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Announcement announcement = Announcement.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdBy(admin)
                .targetAudience(dto.getTargetAudience().toUpperCase())
                .expiresAt(dto.getExpiresAt())
                .build();

        return announcementRepository.save(announcement);
    }

    public List<Announcement> getAll() {
        return announcementRepository.findAll();
    }

    public List<Announcement> getActive() {
        return announcementRepository.findByExpiresAtAfterOrExpiresAtIsNull(LocalDate.now());
    }

    public List<Announcement> getByAudience(String audience) {
        return announcementRepository.findByTargetAudience(audience.toUpperCase());
    }
}
