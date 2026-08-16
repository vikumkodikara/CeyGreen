package com.ceygreen.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class OpenRouterClient {

    private static final Logger log = LoggerFactory.getLogger(OpenRouterClient.class);
    
    @Value("${openrouter.api-key:dummy}")
    private String apiKey;
    
    @Value("${openrouter.model:meta-llama/llama-3.3-70b-instruct:free}")
    private String model;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    
    private static final String SYSTEM_PROMPT = "You are a strict agricultural AI assistant for CeyGreen. You must ONLY rely on verified public agricultural practices and data. Do not invent treatments, hallucinate facts, or guess. If you do not know the answer with 100% certainty, state: 'I cannot verify this based on available agricultural data.' Do not discuss backend systems, microservices, or private records. Keep your answers concise and highly factual.";

    public String generateChatResponse(String userMessage, List<Map<String, String>> history) {
        if ("dummy".equals(apiKey) || apiKey.isBlank()) {
            return "Please configure the OPENROUTER_API_KEY in the environment.";
        }
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        
        if (history != null) {
            for (Map<String, String> msg : history) {
                if (msg.containsKey("role") && msg.containsKey("content")) {
                    messages.add(Map.of("role", msg.get("role"), "content", msg.get("content")));
                }
            }
        }
        
        messages.add(Map.of("role", "user", "content", userMessage));
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        
        return callOpenRouter(requestBody);
    }
    
    public List<String> generateDynamicInsights(String context) {
        if ("dummy".equals(apiKey) || apiKey.isBlank()) {
            return List.of("Configure OPENROUTER_API_KEY for dynamic insights", "Tomato blight prevention", "Smart irrigation trends");
        }
        
        String prompt = "Based on the following context, generate exactly 4 short, actionable insight topics (maximum 6 words each) for an agricultural dashboard. Format as a comma-separated list. Context: " + (context != null ? context : "general greenhouse farming");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
            Map.of("role", "system", "content", "You are an AI that only generates short, comma-separated lists of agricultural topics."),
            Map.of("role", "user", "content", prompt)
        ));
        
        String response = callOpenRouter(requestBody);
        
        if (response != null && !response.isBlank()) {
            List<String> insights = new ArrayList<>();
            for (String part : response.split(",")) {
                if (!part.trim().isEmpty()) {
                    insights.add(part.trim());
                }
            }
            return insights.size() > 0 ? insights : List.of("Tomato care", "Irrigation", "Pest control");
        }
        
        return List.of("Automated watering", "Crop rotation", "Soil testing", "Pest identification");
    }

    private String callOpenRouter(Map<String, Object> requestBody) {
        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + apiKey);
            request.getHeaders().add("Content-Type", "application/json");
            request.getHeaders().add("HTTP-Referer", "http://localhost:3000"); // OpenRouter requires referer
            request.getHeaders().add("X-Title", "CeyGreen AI");
            return execution.execute(request, body);
        });

        try {
            Map response = restTemplate.postForObject(API_URL, requestBody, Map.class);
            if (response != null && response.containsKey("choices")) {
                List choices = (List) response.get("choices");
                if (!choices.isEmpty()) {
                    Map choice = (Map) choices.get(0);
                    Map message = (Map) choice.get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            log.error("OpenRouter API call failed", e);
            return "Sorry, I am currently experiencing connection issues.";
        }
        return "No response generated.";
    }
}
