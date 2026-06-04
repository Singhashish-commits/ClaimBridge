package com.ashish.claimbridgegateway.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class GatewayMatrixFilter implements GlobalFilter, Ordered {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeRequestsGauge;

    public GatewayMatrixFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.activeRequestsGauge = meterRegistry.gauge("gateway.requests.active", new AtomicInteger(0));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long startTime = System.nanoTime();

        activeRequestsGauge.incrementAndGet();

        return chain.filter(exchange).doOnError(throwable -> {
            // Decrement gauge if an unhandled error occurs mid-flight
            activeRequestsGauge.decrementAndGet();
        }).then(Mono.fromRunnable(() -> {
            // 2. Decrement active requests gauge on successful completion
            activeRequestsGauge.decrementAndGet();

            long durationNanos = System.nanoTime() - startTime;

            // Extract metadata tags
            String method = exchange.getRequest().getMethod().name();

            // 🚨 FIX APPLIED HERE: Extract the Route object safely, then get its ID
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            String routeId = (route != null) ? route.getId() : "unknown_route";

            ServerHttpResponse response = exchange.getResponse();
            String statusCode = response.getStatusCode() != null ? response.getStatusCode().toString() : "UNKNOWN";

            // 3. Record Counter Metric
            Counter.builder("gateway.requests.total")
                    .description("Total number of requests routed through the gateway")
                    .tag("method", method)
                    .tag("routeId", routeId)
                    .tag("status", statusCode)
                    .register(meterRegistry)
                    .increment();

            // 4. Record Timer Metric
            Timer.builder("gateway.requests.duration")
                    .description("Time taken to route request and receive response")
                    .tag("routeId", routeId)
                    .tag("status", statusCode)
                    .register(meterRegistry)
                    .record(durationNanos, TimeUnit.NANOSECONDS);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
