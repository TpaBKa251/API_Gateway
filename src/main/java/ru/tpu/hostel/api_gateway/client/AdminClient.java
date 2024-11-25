package ru.tpu.hostel.api_gateway.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.dto.BalanceResponseDto;
import ru.tpu.hostel.api_gateway.dto.CertificateDto;
import ru.tpu.hostel.api_gateway.enums.DocumentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

    public Flux<BalanceResponseDto> getAllBalances(
            Integer page,
            Integer size,
            Boolean negative,
            BigDecimal value
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("balance/get/all")
                        .queryParam("page", page != null ? page : 0)
                        .queryParam("size", size != null ? size : 1000000000)
                        .queryParam("negative", negative)
                        .queryParam("value", value)
                        .build())
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

    public Flux<CertificateDto> getAllDocuments(
            Integer page,
            Integer size,
            Boolean fluraPast,
            LocalDate fluraDate,
            Boolean certPast,
            LocalDate certDate
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("documents/get/all")
                        .queryParam("page", page != null ? page : 0)
                        .queryParam("size", size != null ? size : 1000000000)
                        .queryParam("fluraPast", fluraPast)
                        .queryParam("fluraDate", fluraDate)
                        .queryParam("certPast", certPast)
                        .queryParam("certDate", certDate)
                        .build())
                .retrieve()
                .bodyToFlux(CertificateDto.class);
    }

    public Flux<CertificateDto> getAllDocumentsByUsers(@RequestParam List<UUID> userIds) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("documents/get/all/by/users")
                        .queryParam("userIds", userIds.toArray())
                        .build())
                .retrieve()
                .bodyToFlux(CertificateDto.class);
    }

    public Flux<BalanceResponseDto> getAllBalancesByUsers(@RequestParam List<UUID> userIds) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("balance/get/all/by/users")
                        .queryParam("userIds", userIds.toArray())
                        .build())
                .retrieve()
                .bodyToFlux(BalanceResponseDto.class);
    }
}

