package com.jobtrackerapp.jobtracker.service;

import com.jobtrackerapp.jobtracker.dto.DashboardResponse;
import com.jobtrackerapp.jobtracker.entity.ApplicationStatus;
import com.jobtrackerapp.jobtracker.entity.JobApplication;
import com.jobtrackerapp.jobtracker.entity.User;
import com.jobtrackerapp.jobtracker.repository.AIAnalysisRepository;
import com.jobtrackerapp.jobtracker.repository.JobApplicationRepository;
import com.jobtrackerapp.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

    private final JobApplicationRepository jobApplicationRepository;
    private final AIAnalysisRepository aiAnalysisRepository;
    private final UserRepository userRepository;

    public DashboardResponse getDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<JobApplication> applications = jobApplicationRepository
                .findByUserId(user.getId());

        DashboardResponse response = new DashboardResponse();

        // Total applications
        response.setTotalApplications(applications.size());

        // Status breakdown
        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            long count = applications.stream()
                    .filter(a -> a.getStatus() == status)
                    .count();
            if (count > 0) {
                statusBreakdown.put(status.name(), count);
            }
        }
        response.setStatusBreakdown(statusBreakdown);

        // Interview count (INTERVIEW + TECHNICAL_ROUND + HR_ROUND)
        long interviewCount = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.INTERVIEW
                        || a.getStatus() == ApplicationStatus.TECHNICAL_ROUND
                        || a.getStatus() == ApplicationStatus.HR_ROUND
                        || a.getStatus() == ApplicationStatus.OFFER
                        || a.getStatus() == ApplicationStatus.ACCEPTED)
                .count();
        response.setInterviewCount(interviewCount);

        // Interview rate
        double interviewRate = applications.isEmpty() ? 0 :
                Math.round((interviewCount * 100.0 / applications.size()) * 10.0) / 10.0;
        response.setInterviewRate(interviewRate);

        // Average match score from AI analyses
        List<Long> applicationIds = applications.stream()
                .map(JobApplication::getId)
                .collect(Collectors.toList());

        long analyzedCount = applicationIds.stream()
                .filter(id -> aiAnalysisRepository.findByJobApplicationId(id).isPresent())
                .count();
        response.setAnalyzedApplications(analyzedCount);

        double avgScore = applicationIds.stream()
                .map(id -> aiAnalysisRepository.findByJobApplicationId(id))
                .filter(opt -> opt.isPresent())
                .mapToInt(opt -> opt.get().getMatchScore())
                .average()
                .orElse(0.0);
        response.setAverageMatchScore(Math.round(avgScore * 10.0) / 10.0);

        return response;
    }
}