package ru.tpu.hostel.api_gateway.configurtion.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest.base-url")
public record WebClientProperties(

        String userService,

        String bookingService,

        String scheduleService,

        String adminService
) {
}
