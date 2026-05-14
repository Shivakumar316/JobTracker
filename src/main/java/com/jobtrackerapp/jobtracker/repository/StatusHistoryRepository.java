package com.jobtrackerapp.jobtracker.repository;

import com.jobtrackerapp.jobtracker.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByJobApplicationIdOrderByChangedAtDesc(Long applicationId);

    void deleteByJobApplicationId(Long applicationId);
}