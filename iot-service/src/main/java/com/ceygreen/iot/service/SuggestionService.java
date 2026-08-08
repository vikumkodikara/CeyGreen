package com.ceygreen.iot.service;

import com.ceygreen.iot.dto.SuggestionResponse;
import com.ceygreen.iot.repository.TelemetryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Returns current suggestions for a greenhouse.
 * The rule engine will populate suggestions when readings are ingested (later step).
 */
@Service
public class SuggestionService {

    private final TelemetryRepository telemetryRepository;

    public SuggestionService(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    public List<SuggestionResponse> listByGreenhouse(String greenhouseId) {
        telemetryRepository.findGreenhouse(greenhouseId)
                .orElseThrow(() -> new IllegalArgumentException("Greenhouse not found: " + greenhouseId));

        return telemetryRepository.findSuggestions(greenhouseId).stream()
                .map(SuggestionResponse::from)
                .toList();
    }
}
