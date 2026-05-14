package com.jobtrackerapp.jobtracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_analysis")
@Data
public class AIAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication jobApplication;

    private Integer matchScore;

    @Column(columnDefinition = "TEXT")
    private String matchedSkills;   // stored as comma-separated string

    @Column(columnDefinition = "TEXT")
    private String missingSkills;   // stored as comma-separated string

    @Column(columnDefinition = "TEXT")
    private String suggestions;     // stored as JSON string

    @Column(columnDefinition = "TEXT")
    private String summary;

    private LocalDateTime analyzedAt;

    @PrePersist
    protected void onCreate() {
        analyzedAt = LocalDateTime.now();
    }
}