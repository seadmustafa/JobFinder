package com.ai.jobfinder.repository;

import com.ai.jobfinder.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByUrl(String url);
    List<Job> findByNotificationSentFalse();
    List<Job> findByTitleContainingIgnoreCase(String title);
}