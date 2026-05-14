package com.jobtrackerapp.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobApplicationRequest {

    @NotBlank
    private String companyName;

    @NotBlank
    private String roleName;

    private String jobDescription;
}