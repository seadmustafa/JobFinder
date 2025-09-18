package com.ai.jobfinder.controller;

import com.ai.jobfinder.entity.Job;
import com.ai.jobfinder.service.JobFinderService;
import com.ai.jobfinder.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
    
    private final JobFinderService jobFinderService;
    private final WhatsAppService whatsAppService;
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobFinderService.getAllJobs());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(@RequestParam String title) {
        return ResponseEntity.ok(jobFinderService.searchJobs(title));
    }
    
    @PostMapping("/find")
    public ResponseEntity<String> triggerJobSearch() {
        jobFinderService.findAndNotifyJobs();
        return ResponseEntity.ok("Job search triggered successfully");
    }


    @GetMapping("/developer-jobs")
    public ResponseEntity<List<Job>> getDeveloperJobs() {
        List<Job> jobs = jobFinderService.getDeveloperJobsAndNotify();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/duapune-jobs")
    public ResponseEntity<List<Job>> getDuapuneJobs() {
        List<Job> jobs = jobFinderService.getDuapuneJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/linkedin-jobs")
    public ResponseEntity<List<Job>> getLinkedInJobs() {
        List<Job> jobs = jobFinderService.getLinkedInJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/all-sources")
    public ResponseEntity<Map<String, List<Job>>> getAllSourceJobs() {
        Map<String, List<Job>> allJobs = new HashMap<>();
        allJobs.put("njoftime", jobFinderService.getDeveloperJobsAndNotify());
        allJobs.put("duapune", jobFinderService.getDuapuneJobs());
        allJobs.put("linkedin", jobFinderService.getLinkedInJobs());
        return ResponseEntity.ok(allJobs);
    }
}