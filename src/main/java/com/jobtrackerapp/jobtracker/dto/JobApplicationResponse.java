package com.jobtrackerapp.jobtracker.dto;

import com.jobtrackerapp.jobtracker.entity.ApplicationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobApplicationResponse {
    private Long id;
    private String companyName;
    private String roleName;
    private String jobDescription;
    private ApplicationStatus status;
    private LocalDateTime appliedDate;
    private LocalDateTime createdAt;
}