package ru.tpu.hostel.api_gateway.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.dto.BalanceResponseDto;
import ru.tpu.hostel.api_gateway.dto.CertificateDto;
import ru.tpu.hostel.api_gateway.enums.DocumentType;

import java.util.UUID;

@Component
public class AdminClient {

    private final WebClient webClient;

    public AdminClient(@Qualifier("adminWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<?> getBalanceShort(UUID id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("balance/get/short/{id}").build(id))
                .retrieve()
                .toEntity(Object.class);
    }

    public Flux<BalanceResponseDto> getAllBalances() {
        return webClient.get()
                .uri("balance/get/all")
                .retrieve()
                .bodyToFlux(BalanceResponseDto.class);
    }

    public Mono<CertificateDto> getDocumentByType(UUID userId, DocumentType documentType) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("documents/get/type/{documentType}/user/{userId}")
                        .build(documentType, userId))
                .retrieve()
                .bodyToMono(CertificateDto.class);
    }

    public Flux<CertificateDto> getAllDocuments() {
        return webClient.get()
                .uri("documents/get/all")
                .retrieve()
                .bodyToFlux(CertificateDto.class);
    }
}

