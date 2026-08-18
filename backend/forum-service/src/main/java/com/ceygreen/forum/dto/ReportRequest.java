package com.ceygreen.forum.dto;

import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
        @NotBlank(message = "Report type is required") String reportType
) {
}
