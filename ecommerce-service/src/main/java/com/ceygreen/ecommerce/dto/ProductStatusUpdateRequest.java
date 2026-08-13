package com.ceygreen.ecommerce.dto;

import jakarta.validation.constraints.NotNull;

public record ProductStatusUpdateRequest(@NotNull Boolean active) {}
