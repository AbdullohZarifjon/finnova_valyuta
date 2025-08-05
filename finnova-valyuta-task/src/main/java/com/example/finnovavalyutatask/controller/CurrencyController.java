package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.payload.ApiResponse;
import com.example.finnovavalyutatask.payload.ApiResponseFactory;
import com.example.finnovavalyutatask.payload.dto.CurrencyDto;
import com.example.finnovavalyutatask.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/currency")
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/{date}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: has permission")
    public ResponseEntity<ApiResponse<List<CurrencyDto>>> getCurrency(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                      LocalDate date) {
        List<CurrencyDto> currencyList = currencyService.getCurrency(date);
        return ApiResponseFactory.success("Valyutalar ro'yxati", currencyList);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: has permission")
    public ResponseEntity<ApiResponse<List<CurrencyDto>>> getTodayCurrency() {
        List<CurrencyDto> currencyList = currencyService.getCurrency(null);
        return ApiResponseFactory.success("Bugungi valyutalar ro'yxati", currencyList);
    }

//    @GetMapping({"/", "/{date}"})
//    public ResponseEntity<List<CurrencyEntity>> getCurrency(
//            @PathVariable(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        return ResponseEntity.ok(currencyService.getCurrency(date));
//    }


}
