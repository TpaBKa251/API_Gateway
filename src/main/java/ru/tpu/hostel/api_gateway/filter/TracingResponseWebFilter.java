package ru.tpu.hostel.api_gateway.filter;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TracingResponseWebFilter implements WebFilter {

    @SuppressWarnings("NullableProblems")
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Span span = Span.current();
        if (span == null) {
            return chain.filter(exchange);
        }

        SpanContext sc = span.getSpanContext();
        if (sc == null) {
            return chain.filter(exchange);
        }

        String traceId = sc.isValid()
                ? sc.getTraceId()
                : null;
        if (StringUtils.hasText(traceId)) {
            exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
        }

        return chain.filter(exchange);
    }

}
