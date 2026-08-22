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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/iot/greenhouses")
@Tag(name = "Greenhouses", description = "Register a greenhouse blueprint (zones + ESP32).")
@SecurityRequirement(name = "apiKey")
public class GreenhouseController {

    private final GreenhouseService greenhouseService;

    public GreenhouseController(GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }

    @GetMapping
    @Operation(summary = "List my greenhouses", description = "Returns houses registered to this farmer so login can restore the view.")
    public List<GreenhouseResponse> mine(@RequestParam String farmerId) {
        return greenhouseService.listMine(farmerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create greenhouse", description = "Registers zones and one ESP32 device per zone.")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Validation failed or greenhouse already exists")
    public GreenhouseResponse create(@Valid @RequestBody CreateGreenhouseRequest request) {
        return greenhouseService.create(request);
    }

    @DeleteMapping("/{greenhouseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Unregister greenhouse", description = "Removes this farmer's house so it can be registered again for a demo.")
    public void remove(
            @PathVariable String greenhouseId,
            @RequestParam String farmerId) {
        greenhouseService.remove(greenhouseId, farmerId);
    }
}
