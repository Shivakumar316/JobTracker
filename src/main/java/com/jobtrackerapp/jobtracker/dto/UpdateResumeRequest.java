package com.jobtrackerapp.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateResumeRequest {
    @NotBlank
    private String resumeText;
}