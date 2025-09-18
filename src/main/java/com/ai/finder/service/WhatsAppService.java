package com.ai.jobfinder.service;

import com.ai.jobfinder.entity.Job;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppService {
    
    @Value("${twilio.account.sid}")
    private String accountSid;
    
    @Value("${twilio.auth.token}")
    private String authToken;
    
    @Value("${twilio.whatsapp.from}")
    private String fromNumber;
    
    @Value("${twilio.whatsapp.to}")
    private String toNumber;
    
    private final JobAnalysisService jobAnalysisService;
    
    public WhatsAppService(JobAnalysisService jobAnalysisService) {
        this.jobAnalysisService = jobAnalysisService;
    }

    public void sendJobNotification(Job job) {
        try {
            log.info("Initializing Twilio with SID: {}", accountSid);
            Twilio.init(accountSid, authToken);

            String summary = jobAnalysisService.generateJobSummary(job.getDescription());

            String messageBody = String.format("""
            🔍 *New Job Found!*
            
            *Title:* %s
            *Company:* %s
            
            *Summary:* %s
            
            *Link:* %s
            """, job.getTitle(), job.getCompany(), summary, job.getUrl());

            log.info("Sending WhatsApp from: whatsapp:{} to: whatsapp:{}", fromNumber, toNumber);

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + toNumber),
                    new PhoneNumber("whatsapp:" + fromNumber),
                    messageBody
            ).create();

            log.info("WhatsApp message sent successfully. SID: {}", message.getSid());
            log.info("Message status: {}", message.getStatus());

        } catch (Exception e) {
            log.error("Error sending WhatsApp notification for job: {}", job.getTitle(), e);
            log.error("From number: {}, To number: {}", fromNumber, toNumber);
            log.error("Account SID: {}", accountSid);
        }
    }
}