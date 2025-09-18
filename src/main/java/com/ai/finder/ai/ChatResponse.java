package com.ai.jobfinder.ai;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ChatResponse {
    private final Map<String, Object> rawResponse;
    
    public ChatResponse(Map<String, Object> response) {
        this.rawResponse = response;
    }
    
    public Result getResult() {
        return new Result(rawResponse);
    }
    
    @Data
    public static class Result {
        private final Map<String, Object> data;
        
        public Result(Map<String, Object> data) {
            this.data = data;
        }
        
        public Output getOutput() {
            return new Output(data);
        }
    }
    
    @Data
    public static class Output {
        private final Map<String, Object> data;
        
        public Output(Map<String, Object> data) {
            this.data = data;
        }
        
        public String getContent() {
            try {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) data.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            } catch (Exception e) {
                // Fallback to empty string if parsing fails
            }
            return "";
        }
    }
}