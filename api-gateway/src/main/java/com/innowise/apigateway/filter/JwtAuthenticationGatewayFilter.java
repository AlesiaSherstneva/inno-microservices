package com.innowise.apigateway.filter;

import com.innowise.securitystarter.jwt.JwtProvider;
import com.innowise.securitystarter.util.SecurityConstant;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

@Setter
@Component
@ConfigurationProperties(prefix = "gateway.security")
public class JwtAuthenticationGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilter.Config> {
    private final JwtProvider jwtProvider;
    private List<String> publicPaths = new ArrayList<>();

    public JwtAuthenticationGatewayFilter(JwtProvider jwtProvider) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();

            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }

            String token = resolveToken(exchange);

            if (token == null || !jwtProvider.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    private boolean isPublicEndpoint(String path) {
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    private String resolveToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(SecurityConstant.AUTH_HEADER);
        if (bearerToken != null && bearerToken.startsWith(SecurityConstant.BEARER_PREFIX)) {
            return bearerToken.substring(SecurityConstant.BEARER_PREFIX.length()).trim();
        }
        return null;
    }

    public static class Config {
    }
}