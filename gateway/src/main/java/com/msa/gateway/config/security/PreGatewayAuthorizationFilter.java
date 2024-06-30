package com.msa.gateway.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.gateway.error.Error;
import com.msa.gateway.error.ErrorSpec;
import com.msa.gateway.error.ErrorVO;
import com.msa.gateway.util.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class PreGatewayAuthorizationFilter extends AbstractGatewayFilterFactory<PreGatewayAuthorizationFilter.Config> {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    public PreGatewayAuthorizationFilter(JwtProvider jwtProvider, ObjectMapper objectMapper){
        super(Config.class);
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
    }

    @Getter
    public static class Config{
        private final String headerName = Constants.AUTHORIZATION;
        private final String granted = Constants.TOKEN_TYPE;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            final String token = getToken(exchange, config);

            if (ObjectUtils.isEmpty(token)) {
                return unauthorizedResponse(exchange);
            }

            if (!jwtProvider.isValidToken(token)) {
                return unauthorizedResponse(exchange);
            }

            return chain.filter(exchange);
        };
    }

    private String getToken(ServerWebExchange exchange, Config config) {
        final String token = exchange.getRequest().getHeaders().getFirst(config.headerName);
        if (StringUtils.hasText(token)) {
            return token.replace(Constants.TOKEN_TYPE + " ", "");
        }
        return null;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorVO errorVO = ErrorVO.of(Error.of(ErrorSpec.InvalidTokenError));

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorVO);
        } catch (JsonProcessingException e) {
            log.error("Error serializing ErrorVO", e);
            bytes = new byte[0];
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
