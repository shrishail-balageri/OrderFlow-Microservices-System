package com.example.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder,
                               RedisRateLimiter redisRateLimiter,
                               KeyResolver userKeyResolver) {
        return builder.routes()

            .route("user-service", r -> r
                .path("/users/**")
                .filters(f -> f
                    .requestRateLimiter(c -> {
                        c.setRateLimiter(redisRateLimiter);
                        c.setKeyResolver(userKeyResolver);
                        c.setDenyEmptyKey(false);  // don't reject if key is somehow empty
                    })
                    .circuitBreaker(c -> c
                        .setName("userServiceCB")
                        .setFallbackUri("forward:/fallback/users"))
                
                )
                .uri("http://localhost:8081"))

            .route("product-service", r -> r
                .path("/orders/**")
                .filters(f -> f
                    .requestRateLimiter(c -> {
                        c.setRateLimiter(redisRateLimiter);
                        c.setKeyResolver(userKeyResolver);
                        c.setDenyEmptyKey(false);
                    })
                    .circuitBreaker(c -> c
                        .setName("productServiceCB")
                        .setFallbackUri("forward:/fallback/orders"))
                )
                .uri("http://localhost:8082"))

            .build();
    }
}
