package com.jobtracker.api.repository;

import com.jobtracker.api.model.ApplicationStatus;
import com.jobtracker.api.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStatus(ApplicationStatus status);

    List<JobApplication> findByDateAppliedBetween(LocalDate from, LocalDate to);

    List<JobApplication> findByStatusAndDateAppliedBetween(ApplicationStatus status, LocalDate from, LocalDate to);

    @Query("SELECT j FROM JobApplication j WHERE " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.role) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<JobApplication> fullTextSearch(@Param("query") String query);

    @Query("SELECT j.status, COUNT(j) FROM JobApplication j GROUP BY j.status")
    List<Object[]> countByStatus();
}
