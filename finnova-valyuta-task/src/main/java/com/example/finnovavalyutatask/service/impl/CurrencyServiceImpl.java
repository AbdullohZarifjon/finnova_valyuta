package com.example.finnovavalyutatask.service.impl;

import com.example.finnovavalyutatask.payload.dto.CurrencyDto;
import com.example.finnovavalyutatask.entity.CurrencyEntity;
import com.example.finnovavalyutatask.mapper.CurrencyMapper;
import com.example.finnovavalyutatask.repository.CurrencyRepository;
import com.example.finnovavalyutatask.service.CurrencyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final RestTemplate restTemplate;
    private final CurrencyMapper currencyMapper;
    private final CurrencyRepository currencyRepository;

    @Value("${currency.api.url}")
    private String currencyApiUrl;

    public CurrencyServiceImpl(RestTemplate restTemplate, CurrencyMapper currencyMapper, CurrencyRepository currencyRepository) {
        this.restTemplate = restTemplate;
        this.currencyMapper = currencyMapper;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<CurrencyDto> getCurrency(LocalDate date) {
        String apiUrl;
        if (date == null) {
            apiUrl = currencyApiUrl;
        } else {
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            apiUrl = "https://cbu.uz/oz/arkhiv-kursov-valyut/json/all/" + formattedDate + "/";
        }

        CurrencyDto[] currencyRates = restTemplate.getForObject(apiUrl, CurrencyDto[].class);

        if (currencyRates == null || currencyRates.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(currencyRates)
                .sorted(Comparator.comparing(CurrencyDto::getId))
                .toList();
    }

    @Override
    @Scheduled(cron = "${currency.api.cron}")
    public void fetchAndSaveCurrencyRates() {
        System.out.println("Valyuta kurslarini olib kelish boshlandi: " + LocalDateTime.now());

        CurrencyDto[] currencyRates = restTemplate.getForObject(currencyApiUrl, CurrencyDto[].class);

        if (currencyRates != null) {
            for (CurrencyDto rateDto : currencyRates) {
                CurrencyEntity entity = currencyMapper.toEntity(rateDto);
                currencyRepository.save(entity);
            }
            System.out.println("Valyuta kurslari saqlandi.");
        } else {
            System.out.println("API'dan ma'lumot olib kelishda xatolik.");
        }
    }

}

