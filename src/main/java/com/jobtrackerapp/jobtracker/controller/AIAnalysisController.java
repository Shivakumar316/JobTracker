package com.jobtrackerapp.jobtracker.controller;

import com.jobtrackerapp.jobtracker.dto.AIAnalysisResponse;
import com.jobtrackerapp.jobtracker.dto.UpdateResumeRequest;
import com.jobtrackerapp.jobtracker.service.AIAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AIAnalysisController {

    private final AIAnalysisService aiAnalysisService;

    // Update resume text
    @PutMapping("/api/user/resume")
    public ResponseEntity<String> updateResume(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateResumeRequest request) {
        aiAnalysisService.updateResume(userDetails.getUsername(), request);
        return ResponseEntity.ok("Resume updated successfully!");
    }

    // Trigger AI analysis
    @PostMapping("/api/applications/{id}/analyze")
    public ResponseEntity<AIAnalysisResponse> analyze(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(
            aiAnalysisService.analyze(userDetails.getUsername(), id));
    }

    // Get saved analysis
    @GetMapping("/api/applications/{id}/analysis")
    public ResponseEntity<AIAnalysisResponse> getAnalysis(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(
            aiAnalysisService.getAnalysis(userDetails.getUsername(), id));
    }
}