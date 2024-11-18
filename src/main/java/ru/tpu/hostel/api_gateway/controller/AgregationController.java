package ru.tpu.hostel.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.api_gateway.dto.AdminResponseDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;
import ru.tpu.hostel.api_gateway.service.AgregationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Slf4j
public class AgregationController {

    private final AgregationService agregationService;

    @GetMapping("/get/whole/user")
    public WholeUserResponseDto getWholeUser(Authentication authentication) {
        return agregationService.getWholeUser(authentication);
    }

    @GetMapping("/get/all/users")
    public List<AdminResponseDto> getAllUsers(Authentication authentication) {
        return agregationService.getAllUsers(authentication);
    }

}
