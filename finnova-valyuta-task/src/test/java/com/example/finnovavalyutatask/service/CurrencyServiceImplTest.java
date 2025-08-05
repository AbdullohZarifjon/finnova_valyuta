package com.example.finnovavalyutatask.service;

import com.example.finnovavalyutatask.payload.dto.CurrencyDto;
import com.example.finnovavalyutatask.entity.CurrencyEntity;
import com.example.finnovavalyutatask.mapper.CurrencyMapper;
import com.example.finnovavalyutatask.repository.CurrencyRepository;
import com.example.finnovavalyutatask.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class CurrencyServiceImplTest {

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CurrencyMapper currencyMapper;

    @Mock
    private CurrencyRepository currencyRepository;

    private final CurrencyDto sampleDto = CurrencyDto.builder()
            .id(1)
            .ccy("USD")
            .rate(new BigDecimal("12345"))
            .build();

    private final CurrencyEntity sampleEntity = CurrencyEntity.builder()
            .id(1L)
            .currencyCode("USD")
            .rate(new BigDecimal("12345"))
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyService = new CurrencyServiceImpl(restTemplate, currencyMapper, currencyRepository);

        ReflectionTestUtils.setField(currencyService, "currencyApiUrl", "https://mock.api.uz");
    }

    @Test
    void getCurrency_whenDateIsNull_shouldFetchTodayRates() {
        CurrencyDto[] mockResponse = new CurrencyDto[]{sampleDto};
        when(restTemplate.getForObject("https://mock.api.uz", CurrencyDto[].class))
                .thenReturn(mockResponse);

        List<CurrencyDto> result = currencyService.getCurrency(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCcy()).isEqualTo("USD");
    }

    @Test
    void getCurrency_whenDateIsProvided_shouldFetchRatesByDate() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        String expectedUrl = "https://cbu.uz/oz/arkhiv-kursov-valyut/json/all/2023-12-25/";

        CurrencyDto[] mockResponse = new CurrencyDto[]{sampleDto};
        when(restTemplate.getForObject(expectedUrl, CurrencyDto[].class)).thenReturn(mockResponse);

        List<CurrencyDto> result = currencyService.getCurrency(date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCcy()).isEqualTo("USD");
    }

    @Test
    void getCurrency_whenResponseIsNull_shouldReturnEmptyList() {
        when(restTemplate.getForObject(anyString(), eq(CurrencyDto[].class)))
                .thenReturn(null);

        List<CurrencyDto> result = currencyService.getCurrency(null);

        assertThat(result).isEmpty();
    }

    @Test
    void fetchAndSaveCurrencyRates_shouldSaveAllRates() {
        CurrencyDto[] mockResponse = new CurrencyDto[]{sampleDto};
        when(restTemplate.getForObject("https://mock.api.uz", CurrencyDto[].class))
                .thenReturn(mockResponse);

        when(currencyMapper.toEntity(sampleDto)).thenReturn(sampleEntity);

        currencyService.fetchAndSaveCurrencyRates();

        verify(currencyRepository, times(1)).save(sampleEntity);
    }

    @Test
    void fetchAndSaveCurrencyRates_whenApiReturnsNull_shouldDoNothing() {
        when(restTemplate.getForObject(anyString(), eq(CurrencyDto[].class)))
                .thenReturn(null);

        currencyService.fetchAndSaveCurrencyRates();

        verify(currencyRepository, never()).save(any());
    }
}
