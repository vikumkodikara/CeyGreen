package com.ceygreen.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Ensures paginated endpoints return a stable JSON shape ({@code content}, {@code totalElements}, …)
 * that the React client expects.
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {
}
