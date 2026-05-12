package com.ashish.claimbridgegateway.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret.key}")
    private String secretKey;

    private static final List<String> PUBLIC_URLS = List.of(
            "/claimbridge-auth-service/api/auth/login",
            "/claimbridge-auth-service/api/auth/register",
            "/claimbridge-auth-service/api/auth/signup"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (PUBLIC_URLS.stream().anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        // Token check karo
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(
                            Decoders.BASE64.decode(secretKey)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("email", claims.getSubject())
                            .header("role", claims.get("role").toString())
                            .header("tenantId", claims.get("tenantId") != null ? claims.get("tenantId").toString() : "")
                            .header("userId", claims.get("userId") != null ? claims.get("userId").toString() : "")
                            .build())
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
