package com.ceygreen.iot.controller;

import com.ceygreen.iot.dto.CreateGreenhouseRequest;
import com.ceygreen.iot.dto.GreenhouseResponse;
import com.ceygreen.iot.service.GreenhouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iot/greenhouses")
@Tag(name = "Greenhouses", description = "Register a greenhouse blueprint (zones + ESP32).")
@SecurityRequirement(name = "apiKey")
public class GreenhouseController {

    private final GreenhouseService greenhouseService;

    public GreenhouseController(GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create greenhouse", description = "Registers zones and one ESP32 device per zone.")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Validation failed or greenhouse already exists")
    public GreenhouseResponse create(@Valid @RequestBody CreateGreenhouseRequest request) {
        return greenhouseService.create(request);
    }
}
