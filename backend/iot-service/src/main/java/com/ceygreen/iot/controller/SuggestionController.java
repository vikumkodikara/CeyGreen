package com.ceygreen.iot.controller;

import com.ceygreen.iot.dto.SuggestionResponse;
import com.ceygreen.iot.service.SuggestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for greenhouse suggestions shown on the web dashboard.
 */
@RestController
@RequestMapping("/api/iot/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/{greenhouseId}")
    public List<SuggestionResponse> list(@PathVariable String greenhouseId) {
        return suggestionService.listByGreenhouse(greenhouseId);
    }
}
