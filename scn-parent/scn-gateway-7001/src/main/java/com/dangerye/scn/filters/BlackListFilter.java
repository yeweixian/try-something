package com.dangerye.scn.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BlackListFilter implements GlobalFilter, Ordered {

    private static final List<String> IP_BLACK_LIST = new ArrayList<>();

    static {
        IP_BLACK_LIST.add("127.0.0.1");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();

        final String clientIp = request.getRemoteAddress().getHostString();
        log.debug("------> ip: " + clientIp);
        if (IP_BLACK_LIST.contains(clientIp)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            final String data = "request be denied!";
            final DataBuffer dataBuffer = response.bufferFactory().wrap(data.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        } else {
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
