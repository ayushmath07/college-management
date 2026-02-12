package com.landminesoft.CollegeManagement.repository;

import com.landminesoft.CollegeManagement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTargetAudience(String audience);

    List<Announcement> findByExpiresAtAfterOrExpiresAtIsNull(LocalDate date);

    List<Announcement> findByTargetAudienceAndExpiresAtAfterOrTargetAudienceAndExpiresAtIsNull(
            String audience1, LocalDate date, String audience2);
}
