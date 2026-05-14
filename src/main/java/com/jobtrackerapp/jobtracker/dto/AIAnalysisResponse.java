package com.jobtrackerapp.jobtracker.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AIAnalysisResponse {
    private Long id;
    private Integer matchScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> suggestions;
    private String summary;
    private LocalDateTime analyzedAt;
}