package com.ai.jobfinder.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatClient {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    
    @Value("${spring.ai.openai.chat.options.model:gpt-3.5-turbo}")
    private String model;
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    public ChatResponse call(Prompt prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "user", "content", prompt.getContent())
                ),
                "max_tokens", 500,
                "temperature", 0.7
            );
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                OPENAI_API_URL, request, Map.class
            );
            
            return new ChatResponse(response.getBody());
            
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
            throw new RuntimeException("Failed to get AI response", e);
        }
    }
}