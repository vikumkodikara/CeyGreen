package com.ceygreen.ecommerce.controller;

import com.ceygreen.ecommerce.dto.DashboardSummaryResponse;
import com.ceygreen.ecommerce.security.RequestIdentity;
import com.ceygreen.ecommerce.security.UserRole;
import com.ceygreen.ecommerce.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Farmer dashboard")
@SecurityRequirement(name = "apiKey")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> summary(HttpServletRequest request) {
        RequestIdentity.requireRole(request, UserRole.FARMER);
        return ResponseEntity.ok(dashboardService.getSummary(RequestIdentity.requireFarmerId(request)));
    }
}
