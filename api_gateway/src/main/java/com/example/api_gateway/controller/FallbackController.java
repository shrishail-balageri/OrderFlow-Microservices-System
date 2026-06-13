package com.example.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/users")
    public ResponseEntity<Map<String, Object>> usersFallback(ServerWebExchange exchange) {
        return buildFallback(exchange, "user-service", "User Service is currently unavailable.");
    }

    @RequestMapping("/fallback/orders")
    public ResponseEntity<Map<String, Object>> ordersFallback(ServerWebExchange exchange) {
        return buildFallback(exchange, "product-service", "Order Service is currently unavailable.");
    }

    private ResponseEntity<Map<String, Object>> buildFallback(
            ServerWebExchange exchange, String service, String message) {

        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Correlation-Id");

        Map<String, Object> body = Map.of(
            "status", 503,
            "error", "Service Unavailable",
            "message", message,
            "service", service,
            "correlationId", correlationId != null ? correlationId : "unknown",
            "timestamp", Instant.now().toString(),
            "hint", "The circuit breaker is open. Please retry after some time."
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
