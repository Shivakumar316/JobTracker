package com.jobtrackerapp.jobtracker.dto;

import lombok.Data;
import java.util.Map;

@Data
public class DashboardResponse {
    private long totalApplications;
    private Map<String, Long> statusBreakdown;
    private double averageMatchScore;
    private long analyzedApplications;
    private long interviewCount;
    private double interviewRate;
}