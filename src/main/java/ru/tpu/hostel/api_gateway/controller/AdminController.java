package ru.tpu.hostel.api_gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.api_gateway.client.AdminClient;
import ru.tpu.hostel.api_gateway.dto.BalanceResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.request.BalanceRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.request.DocumentEditRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.request.DocumentRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.response.BalanceShortResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.DocumentResponseDto;
import ru.tpu.hostel.api_gateway.enums.DocumentType;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class AdminController {

    private final AdminClient adminClient;

    // Баланс
    @PostMapping("/balance")
    public BalanceResponseDto addBalance(
            @RequestBody @Valid BalanceRequestDto balanceRequestDto
    ) {
        return adminClient.addBalance(balanceRequestDto);
    }

    @PatchMapping("/balance/edit")
    public BalanceResponseDto editBalance(
            @RequestBody @Valid BalanceRequestDto balanceRequestDto
    ) {
        return adminClient.editBalance(balanceRequestDto);
    }

    @PatchMapping("/balance/edit/adding")
    public BalanceResponseDto editAddBalance(
            @RequestBody @Valid BalanceRequestDto balanceRequestDto
    ) {
        return adminClient.editAddBalance(balanceRequestDto);
    }

    @GetMapping("/balance/get/{id}")
    public BalanceResponseDto getBalance(
            @PathVariable UUID id
    ) {
        return adminClient.getBalance(id);
    }

    @GetMapping("/balance/get/all")
    public List<BalanceResponseDto> getAllBalances() {
        return adminClient.getAllBalances();
    }

    @GetMapping("/balance/get/short/{id}")
    public BalanceShortResponseDto getBalanceShort(
            @PathVariable UUID id
    ) {
        return adminClient.getBalanceShort(id);
    }

    // Документы
    @PostMapping("/documents")
    public DocumentResponseDto addDocument(
            @RequestBody @Valid DocumentRequestDto documentRequestDto
    ) {
        return adminClient.addDocument(documentRequestDto);
    }

    @PatchMapping("/documents/edit")
    public DocumentResponseDto editDocument(
            @RequestBody @Valid DocumentEditRequestDto documentEditRequestDto
    ) {
        return adminClient.editDocument(documentEditRequestDto);
    }

    @GetMapping("/documents/get/by/id/{id}")
    public DocumentResponseDto getDocumentById(
            @PathVariable UUID id
    ) {
        return adminClient.getDocumentById(id);
    }

    @GetMapping("/documents/get/all/user/{userId}")
    public List<DocumentResponseDto> getAllDocumentsByUser(
            @PathVariable UUID userId
    ) {
        return adminClient.getAllDocumentsByUser(userId);
    }

    @GetMapping("/documents/get/all")
    public List<DocumentResponseDto> getAllDocuments() {
        return adminClient.getAllDocuments();
    }

    @GetMapping("/documents/get/type/{documentType}/user/{userId}")
    public DocumentResponseDto getDocumentByType(
            @PathVariable UUID userId,
            @PathVariable DocumentType documentType
    ) {
        return adminClient.getDocumentByType(userId, documentType);
    }
}
