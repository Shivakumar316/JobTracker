package com.jobtrackerapp.jobtracker.controller;

import com.jobtrackerapp.jobtracker.dto.JobApplicationRequest;
import com.jobtrackerapp.jobtracker.dto.JobApplicationResponse;
import com.jobtrackerapp.jobtracker.dto.StatusUpdateRequest;
import com.jobtrackerapp.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping
    public ResponseEntity<JobApplicationResponse> addApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.ok(
            jobApplicationService.addApplication(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAllApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
            jobApplicationService.getAllApplications(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(
            jobApplicationService.getApplication(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponse> updateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(
            jobApplicationService.updateStatus(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        jobApplicationService.deleteApplication(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}