package com.jobtrackerapp.jobtracker.dto;

import com.jobtrackerapp.jobtracker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotNull
    private ApplicationStatus status;
}