package com.jobtrackerapp.jobtracker.service;

import com.jobtrackerapp.jobtracker.dto.JobApplicationRequest;
import com.jobtrackerapp.jobtracker.dto.JobApplicationResponse;
import com.jobtrackerapp.jobtracker.dto.StatusUpdateRequest;
import org.springframework.transaction.annotation.Transactional;
import com.jobtrackerapp.jobtracker.entity.*;
import com.jobtrackerapp.jobtracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final AIAnalysisRepository aiAnalysisRepository;

    // Add new application
    public JobApplicationResponse addApplication(String email, JobApplicationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setCompanyName(request.getCompanyName());
        application.setRoleName(request.getRoleName());
        application.setJobDescription(request.getJobDescription());
        application.setStatus(ApplicationStatus.APPLIED);

        jobApplicationRepository.save(application);
        return mapToResponse(application);
    }

    // Get all applications for logged-in user
    public List<JobApplicationResponse> getAllApplications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobApplicationRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get single application
    public JobApplicationResponse getApplication(String email, Long id) {
        JobApplication application = getApplicationAndValidateOwner(email, id);
        return mapToResponse(application);
    }

    // Update status + log history
    public JobApplicationResponse updateStatus(String email, Long id, StatusUpdateRequest request) {
        JobApplication application = getApplicationAndValidateOwner(email, id);

        // Log the status change
        StatusHistory history = new StatusHistory();
        history.setJobApplication(application);
        history.setFromStatus(application.getStatus());
        history.setToStatus(request.getStatus());
        statusHistoryRepository.save(history);

        // Update status
        application.setStatus(request.getStatus());
        jobApplicationRepository.save(application);

        return mapToResponse(application);
    }

    // Delete application
    public void deleteApplication(String email, Long id) {
        JobApplication application = getApplicationAndValidateOwner(email, id);
        
        // Delete child records first, then parent
        statusHistoryRepository.deleteByJobApplicationId(id);
        aiAnalysisRepository.deleteByJobApplicationId(id);
        jobApplicationRepository.delete(application);
    }

    // Helper — fetch and verify the application belongs to this user
    private JobApplication getApplicationAndValidateOwner(String email, Long id) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Use userId comparison instead of accessing lazy user proxy
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return application;
    }

    // Helper — map entity to response DTO
    private JobApplicationResponse mapToResponse(JobApplication app) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(app.getId());
        response.setCompanyName(app.getCompanyName());
        response.setRoleName(app.getRoleName());
        response.setJobDescription(app.getJobDescription());
        response.setStatus(app.getStatus());
        response.setAppliedDate(app.getAppliedDate());
        response.setCreatedAt(app.getCreatedAt());
        return response;
    }
}