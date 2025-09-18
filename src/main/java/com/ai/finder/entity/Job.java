package com.ai.jobfinder.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String company;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String url;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "notification_sent")
    private boolean notificationSent = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}