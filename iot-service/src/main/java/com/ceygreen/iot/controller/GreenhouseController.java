package com.ceygreen.iot.controller;

import com.ceygreen.iot.dto.CreateGreenhouseRequest;
import com.ceygreen.iot.dto.GreenhouseResponse;
import com.ceygreen.iot.service.GreenhouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for greenhouse blueprints.
 * Gateway route: {@code /api/iot/**} → this service on port 8082.
 */
@RestController
@RequestMapping("/api/iot/greenhouses")
public class GreenhouseController {

    private final GreenhouseService greenhouseService;

    public GreenhouseController(GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GreenhouseResponse create(@Valid @RequestBody CreateGreenhouseRequest request) {
        return greenhouseService.create(request);
    }
}
