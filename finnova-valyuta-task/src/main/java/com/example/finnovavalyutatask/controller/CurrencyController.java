package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.dto.CurrencyDto;
import com.example.finnovavalyutatask.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

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
    public ResponseEntity<List<CurrencyDto>> getCurrency(@PathVariable LocalDate date) {
        return ResponseEntity.ok(currencyService.getCurrency(date));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: has permission")
    public ResponseEntity<List<CurrencyDto>> getTodayCurrency() {
        return ResponseEntity.ok(currencyService.getCurrency(null));
    }

//    @GetMapping({"/", "/{date}"})
//    public ResponseEntity<List<CurrencyEntity>> getCurrency(
//            @PathVariable(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        return ResponseEntity.ok(currencyService.getCurrency(date));
//    }


}
