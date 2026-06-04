package com.jobtracker.api.service;

import com.jobtracker.api.dto.CreateApplicationRequest;
import com.jobtracker.api.dto.UpdateApplicationRequest;
import com.jobtracker.api.exception.InvalidStatusTransitionException;
import com.jobtracker.api.exception.ResourceNotFoundException;
import com.jobtracker.api.model.ApplicationStatus;
import com.jobtracker.api.model.JobApplication;
import com.jobtracker.api.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public JobApplication create(CreateApplicationRequest req) {
        JobApplication app = new JobApplication();
        app.setCompany(req.getCompany());
        app.setRole(req.getRole());
        app.setStatus(req.getStatus());
        app.setDateApplied(req.getDateApplied());
        app.setJobUrl(req.getJobUrl());
        app.setLocation(req.getLocation());
        app.setNotes(req.getNotes());
        return repository.save(app);
    }

    public JobApplication getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<JobApplication> getAll(ApplicationStatus status, LocalDate from, LocalDate to) {
        if (status != null && from != null && to != null) {
            return repository.findByStatusAndDateAppliedBetween(status, from, to);
        } else if (status != null) {
            return repository.findByStatus(status);
        } else if (from != null && to != null) {
            return repository.findByDateAppliedBetween(from, to);
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<JobApplication> search(String query) {
        return repository.fullTextSearch(query);
    }

    public JobApplication update(Long id, UpdateApplicationRequest req) {
        JobApplication app = getById(id);
        if (req.getCompany() != null) app.setCompany(req.getCompany());
        if (req.getRole() != null) app.setRole(req.getRole());
        if (req.getDateApplied() != null) app.setDateApplied(req.getDateApplied());
        if (req.getJobUrl() != null) app.setJobUrl(req.getJobUrl());
        if (req.getLocation() != null) app.setLocation(req.getLocation());
        if (req.getNotes() != null) app.setNotes(req.getNotes());
        return repository.save(app);
    }

    public JobApplication transition(Long id, ApplicationStatus newStatus) {
        JobApplication app = getById(id);
        if (!app.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(
                "Cannot transition from " + app.getStatus() + " to " + newStatus);
        }
        app.setStatus(newStatus);
        return repository.save(app);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<ApplicationStatus, Long> getSummary() {
        Map<ApplicationStatus, Long> summary = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus s : ApplicationStatus.values()) {
            summary.put(s, 0L);
        }
        repository.countByStatus().forEach(row -> {
            summary.put((ApplicationStatus) row[0], (Long) row[1]);
        });
        return summary;
    }
}
