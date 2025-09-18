package com.ai.jobfinder.service;

import com.ai.jobfinder.entity.Job;
import com.ai.jobfinder.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobFinderService {

    private final WebScrapingService webScrapingService;
    private final JobAnalysisService jobAnalysisService;
    private final WhatsAppService whatsAppService;
    private final JobRepository jobRepository;

    @Value("${job.search.url}")
    private String jobSearchUrl;

    @Scheduled(cron = "${job.search.cron}")
    public void findAndNotifyJobs() {
        log.info("Starting job search...");

        // Configure your target job titles and skills
        String[] jobTitles = {"Java Developer", "Spring Boot Developer", "Backend Developer"};
        String targetSkills = "Java, Spring Boot, REST API, PostgreSQL, Microservices,Remote, Developer, REMOTE, Programues, Developer, Gjermani";

        for (String jobTitle : jobTitles) {
            searchJobsForTitle(jobTitle, targetSkills);
        }

        // Send notifications for new relevant jobs
        sendNotificationsForNewJobs();

        log.info("Job search completed");
    }

    private void searchJobsForTitle(String jobTitle, String targetSkills) {
        List<WebScrapingService.JobData> scrapedJobs = webScrapingService.scrapeJobs(jobSearchUrl, jobTitle);

        for (WebScrapingService.JobData jobData : scrapedJobs) {
            // Check if job already exists
            if (jobRepository.findByUrl(jobData.url()).isPresent()) {
                continue;
            }

            // Analyze job relevance with AI
            if (jobAnalysisService.isJobRelevant(jobData.description(), targetSkills)) {
                Job job = new Job();
                job.setTitle(jobData.title());
                job.setCompany(jobData.company());
                job.setDescription(jobData.description());
                job.setUrl(jobData.url());

                jobRepository.save(job);
                log.info("Saved relevant job: {} at {}", job.getTitle(), job.getCompany());
            }
        }
    }

    private void sendNotificationsForNewJobs() {
        List<Job> newJobs = jobRepository.findByNotificationSentFalse();

        for (Job job : newJobs) {
            whatsAppService.sendJobNotification(job);
            job.setNotificationSent(true);
            jobRepository.save(job);
        }

        log.info("Sent {} job notifications", newJobs.size());
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> searchJobs(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title);
    }


    public List<Job> getDeveloperJobsAndNotify() {
        log.info("Fetching developer jobs from njoftime.com...");

        String developerJobsUrl = "https://www.njoftime.com/forums/ofroj-vende-pune.14/?last_days=7&keywords=developer";
        String targetSkills = "Java, Spring Boot, REST API, PostgreSQL, Microservices, Developer, Gjermani, Programues, Remote, REMOTE, Italisht";

        // Scrape jobs from the URL
        List<WebScrapingService.JobData> scrapedJobs = webScrapingService.scrapeJobs(developerJobsUrl, "developer");
        List<Job> newJobs = new ArrayList<>();

        for (WebScrapingService.JobData jobData : scrapedJobs) {
            // Check if job already exists
            if (jobRepository.findByUrl(jobData.url()).isPresent()) {
                continue;
            }

            // Analyze job relevance with AI
            Job job = new Job();
            job.setTitle(jobData.title());
            job.setCompany(jobData.company());
            job.setDescription(jobData.description());
            job.setUrl(jobData.url());

            Job savedJob = jobRepository.save(job);
            newJobs.add(savedJob);
            log.info("Saved new developer job: {} at {}", job.getTitle(), job.getCompany());

            // Send immediate WhatsApp notification
            whatsAppService.sendJobNotification(savedJob);
            savedJob.setNotificationSent(true);
            jobRepository.save(savedJob);

        }

        log.info("Found and processed {} new developer jobs", newJobs.size());
        return newJobs;
    }

    public List<Job> getDuapuneJobs() {
        log.info("Fetching developer jobs from duapune.com...");

        String duapuneUrl = "https://duapune.com/kerko-pune?q=developer";
        String targetSkills = "Java, Spring Boot, REST API, PostgreSQL, Microservices, Developer, Backend, Senior";

        List<WebScrapingService.JobData> scrapedJobs = webScrapingService.scrapeDuapuneJobs(duapuneUrl);
        List<Job> newJobs = new ArrayList<>();

        for (WebScrapingService.JobData jobData : scrapedJobs) {
            if (jobRepository.findByUrl(jobData.url()).isPresent()) {
                continue;
            }

            if (jobAnalysisService.isJobRelevant(jobData.description(), targetSkills)) {
                Job job = new Job();
                job.setTitle(jobData.title());
                job.setCompany(jobData.company());
                job.setDescription(jobData.description());
                job.setUrl(jobData.url());

                Job savedJob = jobRepository.save(job);
                newJobs.add(savedJob);
                log.info("Saved duapune job: {} at {}", job.getTitle(), job.getCompany());

                whatsAppService.sendJobNotification(savedJob);
                savedJob.setNotificationSent(true);
                jobRepository.save(savedJob);
            }
        }

        log.info("Found {} new duapune jobs", newJobs.size());
        return newJobs;
    }

    public List<Job> getLinkedInJobs() {
        log.info("Fetching LinkedIn jobs for Tirana and Istanbul...");

        String[] locations = {"Tirana, Albania", "Istanbul, Turkey"};
        String[] jobTitles = {"Senior Backend Developer", "Backend Developer", "Senior Java Developer"};
        String targetSkills = "Java, Spring Boot, REST API, PostgreSQL, Microservices, Backend, Senior";

        List<Job> allNewJobs = new ArrayList<>();

        for (String location : locations) {
            for (String jobTitle : jobTitles) {
                String linkedinUrl = buildLinkedInUrl(jobTitle, location);
                List<WebScrapingService.JobData> scrapedJobs = webScrapingService.scrapeLinkedInJobs(linkedinUrl);

                for (WebScrapingService.JobData jobData : scrapedJobs) {
                    if (jobRepository.findByUrl(jobData.url()).isPresent()) {
                        continue;
                    }

                    if (jobAnalysisService.isJobRelevant(jobData.description(), targetSkills)) {
                        Job job = new Job();
                        job.setTitle(jobData.title());
                        job.setCompany(jobData.company());
                        job.setDescription(jobData.description());
                        job.setUrl(jobData.url());

                        Job savedJob = jobRepository.save(job);
                        allNewJobs.add(savedJob);
                        log.info("Saved LinkedIn job: {} at {} in {}", job.getTitle(), job.getCompany(), location);

                        whatsAppService.sendJobNotification(savedJob);
                        savedJob.setNotificationSent(true);
                        jobRepository.save(savedJob);
                    }
                }
            }
        }

        log.info("Found {} new LinkedIn jobs", allNewJobs.size());
        return allNewJobs;
    }

    private String buildLinkedInUrl(String jobTitle, String location) {
        String encodedTitle = jobTitle.replace(" ", "%20");
        String encodedLocation = location.replace(" ", "%20").replace(",", "%2C");
        return String.format("https://www.linkedin.com/jobs/search/?keywords=%s&location=%s&f_TPR=r86400",
                encodedTitle, encodedLocation);
    }


}