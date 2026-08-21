package com.ceygreen.iot.controller;

import com.ceygreen.iot.dto.SuggestionResponse;
import com.ceygreen.iot.service.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/iot/suggestions")
@Tag(name = "Suggestions", description = "Current rule-engine actions per zone.")
@SecurityRequirement(name = "apiKey")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/{greenhouseId}")
    @Operation(summary = "List suggestions", description = "Recommended actions for the caller's own greenhouse.")
    public List<SuggestionResponse> list(
            @PathVariable String greenhouseId,
            @RequestParam String farmerId) {
        return suggestionService.listByGreenhouse(greenhouseId, farmerId);
    }
}
