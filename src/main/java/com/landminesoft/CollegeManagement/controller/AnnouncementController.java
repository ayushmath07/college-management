package com.landminesoft.CollegeManagement.controller;

import com.landminesoft.CollegeManagement.entity.Announcement;
import com.landminesoft.CollegeManagement.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Public announcement endpoints")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "Get all active announcements")
    public ResponseEntity<List<Announcement>> getActive() {
        return ResponseEntity.ok(announcementService.getActive());
    }

    @GetMapping("/audience/{audience}")
    @Operation(summary = "Get announcements by target audience")
    public ResponseEntity<List<Announcement>> getByAudience(@PathVariable String audience) {
        return ResponseEntity.ok(announcementService.getByAudience(audience));
    }
}
