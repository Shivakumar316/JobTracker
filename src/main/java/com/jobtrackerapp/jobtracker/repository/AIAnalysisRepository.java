package com.jobtrackerapp.jobtracker.repository;

import com.jobtrackerapp.jobtracker.entity.AIAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AIAnalysisRepository extends JpaRepository<AIAnalysis, Long> {
    Optional<AIAnalysis> findByJobApplicationId(Long applicationId);

    void deleteByJobApplicationId(Long applicationId);
}