package com.jobtracker.api.controller;

import com.jobtracker.api.dto.CreateApplicationRequest;
import com.jobtracker.api.dto.StatusTransitionRequest;
import com.jobtracker.api.dto.UpdateApplicationRequest;
import com.jobtracker.api.model.ApplicationStatus;
import com.jobtracker.api.model.JobApplication;
import com.jobtracker.api.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<JobApplication> getAll(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getAll(status, from, to);
    }

    @GetMapping("/search")
    public List<JobApplication> search(@RequestParam String query) {
        return service.search(query);
    }

    @GetMapping("/summary")
    public Map<ApplicationStatus, Long> getSummary() {
        return service.getSummary();
    }

    @GetMapping("/{id}")
    public JobApplication getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<JobApplication> create(@Valid @RequestBody CreateApplicationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PatchMapping("/{id}")
    public JobApplication update(@PathVariable Long id, @RequestBody UpdateApplicationRequest req) {
        return service.update(id, req);
    }

    @PatchMapping("/{id}/status")
    public JobApplication transition(@PathVariable Long id,
                                     @Valid @RequestBody StatusTransitionRequest req) {
        return service.transition(id, req.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
