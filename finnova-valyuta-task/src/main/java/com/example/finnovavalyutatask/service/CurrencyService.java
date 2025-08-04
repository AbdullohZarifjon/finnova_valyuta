package com.example.finnovavalyutatask.service;

import com.example.finnovavalyutatask.dto.CurrencyDto;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyService {

    List<CurrencyDto> getCurrency(LocalDate date);

    void fetchAndSaveCurrencyRates();
}
