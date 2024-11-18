package ru.tpu.hostel.api_gateway.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tpu.hostel.api_gateway.dto.BalanceResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.request.BalanceRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.request.DocumentEditRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.request.DocumentRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.response.BalanceShortResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.DocumentResponseDto;
import ru.tpu.hostel.api_gateway.enums.DocumentType;

import java.util.List;
import java.util.UUID;

@Component
@FeignClient(name = "administration-administrationservice", url = "http://administrationservice:8080")
public interface AdminClient {

    // Балансы
    @PostMapping("/balance")
    BalanceResponseDto addBalance(
            @RequestBody BalanceRequestDto balanceRequestDto
    );

    @PatchMapping("/balance/edit")
    BalanceResponseDto editBalance(
            @RequestBody BalanceRequestDto balanceRequestDto
    );

    @PatchMapping("/balance/edit/adding")
    BalanceResponseDto editAddBalance(
            @RequestBody BalanceRequestDto balanceRequestDto
    );

    @GetMapping("/balance/get/{id}")
    BalanceResponseDto getBalance(
            @PathVariable UUID id
    );

    @GetMapping("/balance/get/all")
    List<BalanceResponseDto> getAllBalances();

    @GetMapping("/balance/get/short/{id}")
    BalanceShortResponseDto getBalanceShort(
            @PathVariable UUID id
    );

    // Документы
    @PostMapping("/documents")
    DocumentResponseDto addDocument(
            @RequestBody DocumentRequestDto documentRequestDto
    );

    @PatchMapping("/documents/edit")
    DocumentResponseDto editDocument(
            @RequestBody DocumentEditRequestDto documentEditRequestDto
    );

    @GetMapping("/documents/get/by/id/{id}")
    DocumentResponseDto getDocumentById(
            @PathVariable UUID id
    );

    @GetMapping("/documents/get/all/user/{userId}")
    List<DocumentResponseDto> getAllDocumentsByUser(
            @PathVariable UUID userId
    );

    @GetMapping("/documents/get/all")
    List<DocumentResponseDto> getAllDocuments();

    @GetMapping("/documents/get/type/{documentType}/user/{userId}")
    DocumentResponseDto getDocumentByType(
            @PathVariable UUID userId,
            @PathVariable DocumentType documentType
    );
}

