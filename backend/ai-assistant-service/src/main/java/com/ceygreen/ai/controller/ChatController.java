package com.ceygreen.ai.controller;

import com.ceygreen.ai.service.OpenRouterClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final OpenRouterClient openRouterClient;

    public ChatController(OpenRouterClient openRouterClient) {
        this.openRouterClient = openRouterClient;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message is required"));
        }
        
        List<Map<String, String>> history = (List<Map<String, String>>) request.get("history");
        
        String reply = openRouterClient.generateChatResponse(message, history);
        return ResponseEntity.ok(Map.of("reply", reply));
    }
    
    @GetMapping("/insights")
    public ResponseEntity<List<String>> getInsights(@RequestParam(required = false) String context) {
        List<String> insights = openRouterClient.generateDynamicInsights(context);
        return ResponseEntity.ok(insights);
    }
}
