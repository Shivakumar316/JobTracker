package com.jobtrackerapp.jobtracker.repository;

import com.jobtrackerapp.jobtracker.entity.JobApplication;
import com.jobtrackerapp.jobtracker.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserId(Long userId);
    List<JobApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status);
}