package ru.tpu.hostel.api_gateway.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AgregationRouter {

    @Bean
    public RouterFunction<ServerResponse> agregationRoutes(AgregationController handler) {
        return route(GET("/api/get/whole/user"), handler::getWholeUser)
                .andRoute(POST("/api/get/all/users"), handler::getAllUsers);
    }
}

