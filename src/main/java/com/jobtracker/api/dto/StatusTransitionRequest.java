package com.jobtracker.api.dto;

import com.jobtracker.api.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public class StatusTransitionRequest {

    @NotNull
    private ApplicationStatus status;

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
}
