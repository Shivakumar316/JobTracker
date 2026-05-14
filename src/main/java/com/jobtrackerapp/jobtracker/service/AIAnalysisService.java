package com.jobtrackerapp.jobtracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtrackerapp.jobtracker.dto.AIAnalysisResponse;
import com.jobtrackerapp.jobtracker.dto.UpdateResumeRequest;
import com.jobtrackerapp.jobtracker.entity.AIAnalysis;
import com.jobtrackerapp.jobtracker.entity.JobApplication;
import com.jobtrackerapp.jobtracker.entity.User;
import com.jobtrackerapp.jobtracker.repository.AIAnalysisRepository;
import com.jobtrackerapp.jobtracker.repository.JobApplicationRepository;
import com.jobtrackerapp.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AIAnalysisService {

    private final GeminiService geminiService;
    private final AIAnalysisRepository aiAnalysisRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // Update user's resume text
    public void updateResume(String email, UpdateResumeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setResumeText(request.getResumeText());
        userRepository.save(user);
    }

    // Analyze resume vs job description
    public AIAnalysisResponse analyze(String email, Long applicationId) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getResumeText() == null || user.getResumeText().isBlank()) {
            throw new RuntimeException("Please update your resume text first.");
        }

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (application.getJobDescription() == null || application.getJobDescription().isBlank()) {
            throw new RuntimeException("This application has no job description.");
        }

        // Call Groq API
        String jsonResult = geminiService.analyzeMatch(
                user.getResumeText(),
                application.getJobDescription()
        );

        // Parse and save result
        try {
            JsonNode node = objectMapper.readTree(jsonResult);

            // Delete old analysis if exists
            aiAnalysisRepository.findByJobApplicationId(applicationId)
                    .ifPresent(aiAnalysisRepository::delete);

            AIAnalysis analysis = new AIAnalysis();
            analysis.setJobApplication(application);
            analysis.setMatchScore(node.path("matchScore").asInt());
            analysis.setMatchedSkills(node.path("matchedSkills").toString());
            analysis.setMissingSkills(node.path("missingSkills").toString());
            analysis.setSuggestions(node.path("suggestions").toString());
            analysis.setSummary(node.path("summary").asText());

            aiAnalysisRepository.save(analysis);
            return mapToResponse(analysis);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        }
    }

    // Get saved analysis
    public AIAnalysisResponse getAnalysis(String email, Long applicationId) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AIAnalysis analysis = aiAnalysisRepository.findByJobApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException("No analysis found. Run /analyze first."));

        return mapToResponse(analysis);
    }

    // Map entity to response DTO
    private AIAnalysisResponse mapToResponse(AIAnalysis analysis) {
        AIAnalysisResponse response = new AIAnalysisResponse();
        response.setId(analysis.getId());
        response.setMatchScore(analysis.getMatchScore());
        response.setSummary(analysis.getSummary());
        response.setAnalyzedAt(analysis.getAnalyzedAt());

        try {
            List<String> matched = new ArrayList<>();
            objectMapper.readTree(analysis.getMatchedSkills())
                    .forEach(n -> matched.add(n.asText()));
            response.setMatchedSkills(matched);

            List<String> missing = new ArrayList<>();
            objectMapper.readTree(analysis.getMissingSkills())
                    .forEach(n -> missing.add(n.asText()));
            response.setMissingSkills(missing);

            List<String> suggestions = new ArrayList<>();
            objectMapper.readTree(analysis.getSuggestions())
                    .forEach(n -> suggestions.add(n.asText()));
            response.setSuggestions(suggestions);

        } catch (Exception e) {
            response.setMatchedSkills(List.of());
            response.setMissingSkills(List.of());
            response.setSuggestions(List.of());
        }

        return response;
    }
}