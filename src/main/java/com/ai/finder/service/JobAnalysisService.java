package com.ai.jobfinder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ai.jobfinder.ai.ChatClient;
import com.ai.jobfinder.ai.ChatResponse;
import com.ai.jobfinder.ai.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAnalysisService {
    
    private final ChatClient chatClient;
    
    public boolean isJobRelevant(String jobDescription, String targetSkills) {
        String prompt = String.format("""
            Analyze this job description and determine if it matches these skills/requirements: %s
            
            Job Description: %s
            
            Respond with only 'YES' if it's a good match (70%% or higher relevance) or 'NO' if not.
            """, targetSkills, jobDescription);
        
        try {
            ChatResponse response = chatClient.call(new Prompt(prompt));
            String result = response.getResult().getOutput().getContent().trim().toUpperCase();
            return "YES".equals(result);
        } catch (Exception e) {
            log.error("Error analyzing job relevance", e);
            return false;
        }
    }
    
    public String generateJobSummary(String jobDescription) {
        String prompt = String.format("""
            Create a concise summary (max 100 words) of this job description highlighting:
            - Key responsibilities
            - Required skills
            - Experience level
            
            Job Description: %s
            """, jobDescription);
        
        try {
            ChatResponse response = chatClient.call(new Prompt(prompt));
            return response.getResult().getOutput().getContent().trim();
        } catch (Exception e) {
            log.error("Error generating job summary", e);
            return "Summary not available";
        }
    }
}