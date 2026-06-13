package com.example.api_gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    // Routes that don't need a token
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/", "/public/", "/actuator/health"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Skip filter for public routes
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // Extract token from Authorization header
        String token = extractToken(exchange);

        if (token == null) {
            return rejectRequest(exchange, "Missing Authorization header");
        }

        if (!jwtTokenProvider.validateToken(token)) {
            return rejectRequest(exchange, "Invalid or expired token");
        }

        // Token is valid — extract user info
        String userId   = jwtTokenProvider.getUserId(token);
        String username = jwtTokenProvider.getUsername(token);
        List<String> roles = jwtTokenProvider.getRoles(token);

        log.debug("JWT valid — user={} roles={} path={}", username, roles, path);

        // Inject user info as headers for downstream services
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-User-Id", userId)
                .header("X-Username", username)
                .header("X-User-Roles", String.join(",", roles))
                .build();

        // Continue with the mutated request
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> rejectRequest(ServerWebExchange exchange, String reason) {
        log.warn("Request rejected: {}", reason);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete(); // end request here
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -2; // run before all other filters
    }
}