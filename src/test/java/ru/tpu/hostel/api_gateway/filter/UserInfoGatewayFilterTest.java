package ru.tpu.hostel.api_gateway.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.tpu.hostel.api_gateway.TestData;
import ru.tpu.hostel.api_gateway.service.impl.JwtService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserInfoGatewayFilterTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserInfoGatewayFilter filter;

    @Test
    void filterWhenPrincipalIsPresent() {
        Authentication authentication = TestData.authentication(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));
        when(jwtService.getUserIdFromToken(authentication)).thenReturn(TestData.USER_ID);
        when(jwtService.getRolesFromToken(authentication)).thenReturn(TestData.ROLE_STUDENT);

        MockServerWebExchange base = MockServerWebExchange.from(
                MockServerHttpRequest.get("/bookings/get/all"));
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest()).thenReturn(base.getRequest());
        when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        when(exchange.mutate()).thenReturn(base.mutate());

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filterWhenPrincipalIsEmpty() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/users/get/all"));

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filterAddsUserHeadersWhenPrincipalIsPresent() {
        Authentication authentication = TestData.authentication(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));
        when(jwtService.getUserIdFromToken(authentication)).thenReturn(TestData.USER_ID);
        when(jwtService.getRolesFromToken(authentication)).thenReturn(TestData.ROLE_STUDENT);

        MockServerWebExchange base = MockServerWebExchange.from(
                MockServerHttpRequest.get("/schedules/active"));
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest()).thenReturn(base.getRequest());
        when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        when(exchange.mutate()).thenReturn(base.mutate());

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenAnswer(invocation -> {
            ServerWebExchange decorated = invocation.getArgument(0);
            ServerHttpRequest request = decorated.getRequest();
            assertThat(request.getHeaders().getFirst("X-User-Id")).isEqualTo(TestData.USER_ID.toString());
            assertThat(request.getHeaders().getFirst("X-User-Roles")).isEqualTo(TestData.ROLE_STUDENT);
            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }
}
