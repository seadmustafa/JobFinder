package com.ai.jobfinder.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WebScrapingService {
    
//    public List<JobData> scrapeJobs(String url, String jobTitle) {
//        List<JobData> jobs = new ArrayList<>();
//
//        try {
//            Document doc = Jsoup.connect(url + "?q=" + jobTitle.replace(" ", "+"))
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
//                    .get();
//
//            // Adjust selectors based on the job site structure
//            Elements jobElements = doc.select(".job-listing, .job-card, [data-job-id]");
//
//            for (Element jobElement : jobElements) {
//                try {
//                    String title = extractText(jobElement, "h2, .job-title, [data-job-title]");
//                    String company = extractText(jobElement, ".company, .company-name, [data-company]");
//                    String jobUrl = extractHref(jobElement, "a, .job-link");
//
//                    if (title != null && company != null && jobUrl != null) {
//                        String description = getJobDescription(jobUrl);
//                        jobs.add(new JobData(title, company, description, jobUrl));
//                    }
//                } catch (Exception e) {
//                    log.warn("Error parsing job element: {}", e.getMessage());
//                }
//            }
//        } catch (IOException e) {
//            log.error("Error scraping jobs from URL: {}", url, e);
//        }
//
//        return jobs;
//    }

    public List<JobData> scrapeJobs(String url, String jobTitle) {
        List<JobData> jobs = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // Correct selectors for njoftime.com structure
            Elements jobElements = doc.select(".structItem-cell--main");

            for (Element jobElement : jobElements) {
                try {
                    // Extract title from the link inside structItem-title
                    String title = extractText(jobElement, ".structItem-title a");

                    // Extract company/author from username or meta info
                    String company = extractText(jobElement, ".username, .structItem-minor .username");

                    // Extract job URL from the title link
                    String jobUrl = extractHref(jobElement, ".structItem-title a");

                    if (title != null && !title.isEmpty() && jobUrl != null) {
                        // Make URL absolute if it's relative
                        if (!jobUrl.startsWith("http")) {
                            jobUrl = "https://www.njoftime.com" + jobUrl;
                        }

                        String description = getJobDescription(jobUrl);
                        String finalCompany = (company != null && !company.isEmpty()) ? company : "Company not specified";

                        jobs.add(new JobData(title, finalCompany, description, jobUrl));
                        log.debug("Found job: {} at {}", title, finalCompany);
                    }
                } catch (Exception e) {
                    log.warn("Error parsing job element: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error scraping jobs from URL: {}", url, e);
        }

        log.info("Scraped {} jobs from {}", jobs.size(), url);
        return jobs;
    }

    private String getJobDescription(String jobUrl) {
        try {
            Document doc = Jsoup.connect(jobUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // Try multiple selectors for njoftime.com content
            Element descElement = doc.selectFirst(".message-body, .bbWrapper, .message-content, article .selectToQuote");

            if (descElement != null) {
                String description = descElement.text();
                // Limit description length to avoid too long content
                return description.length() > 500 ? description.substring(0, 500) + "..." : description;
            }

            return "Description not available";
        } catch (IOException e) {
            log.warn("Could not fetch job description from: {}", jobUrl);
            return "Description not available";
        }
    }
    private String extractText(Element element, String selector) {
        Element selected = element.selectFirst(selector);
        return selected != null ? selected.text().trim() : null;
    }
    
    private String extractHref(Element element, String selector) {
        Element selected = element.selectFirst(selector);
        return selected != null ? selected.absUrl("href") : null;
    }
    
    public record JobData(String title, String company, String description, String url) {}

    public List<JobData> scrapeDuapuneJobs(String url) {
        List<JobData> jobs = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // Selectors for duapune.com structure
            Elements jobElements = doc.select(".job-item, .job-card, .listing-item");

            for (Element jobElement : jobElements) {
                try {
                    String title = extractText(jobElement, ".job-title, .title, h3 a, h2 a");
                    String company = extractText(jobElement, ".company-name, .company, .employer");
                    String jobUrl = extractHref(jobElement, ".job-title a, .title a, h3 a, h2 a");

                    if (title != null && !title.isEmpty() && jobUrl != null) {
                        if (!jobUrl.startsWith("http")) {
                            jobUrl = "https://duapune.com" + jobUrl;
                        }

                        String description = getDuapuneJobDescription(jobUrl);
                        String finalCompany = (company != null && !company.isEmpty()) ? company : "Company not specified";

                        jobs.add(new JobData(title, finalCompany, description, jobUrl));
                        log.debug("Found duapune job: {} at {}", title, finalCompany);
                    }
                } catch (Exception e) {
                    log.warn("Error parsing duapune job element: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error scraping duapune jobs from URL: {}", url, e);
        }

        log.info("Scraped {} jobs from duapune.com", jobs.size());
        return jobs;
    }

    public List<JobData> scrapeLinkedInJobs(String url) {
        List<JobData> jobs = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .timeout(15000)
                    .get();

            // LinkedIn job selectors
            Elements jobElements = doc.select(".job-search-card, .jobs-search__results-list li");

            for (Element jobElement : jobElements) {
                try {
                    String title = extractText(jobElement, ".base-search-card__title, h3 a");
                    String company = extractText(jobElement, ".base-search-card__subtitle, .job-search-card__subtitle-link");
                    String jobUrl = extractHref(jobElement, ".base-card__full-link, h3 a");

                    if (title != null && !title.isEmpty() && jobUrl != null) {
                        if (!jobUrl.startsWith("http")) {
                            jobUrl = "https://www.linkedin.com" + jobUrl;
                        }

                        String description = getLinkedInJobDescription(jobUrl);
                        String finalCompany = (company != null && !company.isEmpty()) ? company : "Company not specified";

                        jobs.add(new JobData(title, finalCompany, description, jobUrl));
                        log.debug("Found LinkedIn job: {} at {}", title, finalCompany);
                    }
                } catch (Exception e) {
                    log.warn("Error parsing LinkedIn job element: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error scraping LinkedIn jobs from URL: {}", url, e);
        }

        log.info("Scraped {} jobs from LinkedIn", jobs.size());
        return jobs;
    }

    private String getDuapuneJobDescription(String jobUrl) {
        try {
            Document doc = Jsoup.connect(jobUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            Element descElement = doc.selectFirst(".job-description, .description, .content, .job-details");

            if (descElement != null) {
                String description = descElement.text();
                return description.length() > 500 ? description.substring(0, 500) + "..." : description;
            }

            return "Description not available";
        } catch (IOException e) {
            log.warn("Could not fetch duapune job description from: {}", jobUrl);
            return "Description not available";
        }
    }

    private String getLinkedInJobDescription(String jobUrl) {
        try {
            Document doc = Jsoup.connect(jobUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            Element descElement = doc.selectFirst(".show-more-less-html__markup, .jobs-description__content");

            if (descElement != null) {
                String description = descElement.text();
                return description.length() > 500 ? description.substring(0, 500) + "..." : description;
            }

            return "Description not available";
        } catch (IOException e) {
            log.warn("Could not fetch LinkedIn job description from: {}", jobUrl);
            return "Description not available";
        }
    }
}