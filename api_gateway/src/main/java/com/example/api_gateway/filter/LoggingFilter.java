// filter/LoggingFilter.java
package com.example.api_gateway.filter;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
       

        String correlationId=request.getHeaders().getFirst("X-Correlation-ID");
        if(correlationId==null){
            correlationId=UUID.randomUUID().toString();
        }
        final String finalCorrelationId=correlationId;

        long startTime = System.currentTimeMillis();

        //Inject into downstream request
        ServerHttpRequest mutatedRequest=request.mutate().header("x-Correlation-ID",finalCorrelationId).build();
        ServerWebExchange mutatedExchange =exchange.mutate().request(mutatedRequest).build();

        // ── LOG INCOMING REQUEST ──────────────────────────────────────────
        log.info("[REQUEST]  {} {} | Headers: {} | RemoteIP: {}",
                request.getMethod(),
                request.getURI(),
                request.getHeaders().toSingleValueMap(),
                request.getRemoteAddress() != null ? request.getRemoteAddress().getHostString() : "unknown"
        );

        // ── PASS TO NEXT FILTER, THEN LOG RESPONSE ────────────────────────
        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {

            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;

            log.info("[RESPONSE] {} {} | Status: {} | Duration: {}ms",
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode(),
                    duration
            );
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Run FIRST before all other filters
    }
}
