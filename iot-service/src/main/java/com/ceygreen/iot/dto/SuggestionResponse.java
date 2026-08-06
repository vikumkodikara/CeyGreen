package com.ceygreen.iot.dto;

import com.ceygreen.iot.model.Suggestion;
import java.util.List;

public record SuggestionResponse(
        String greenhouseId,
        List<Suggestion> suggestions) {
}
