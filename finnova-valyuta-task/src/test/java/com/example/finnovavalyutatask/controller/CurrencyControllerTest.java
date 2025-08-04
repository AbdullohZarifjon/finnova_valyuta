package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.dto.CurrencyDto;
import com.example.finnovavalyutatask.service.CurrencyService;
import com.example.finnovavalyutatask.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
@AutoConfigureMockMvc(addFilters = true)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private final CurrencyDto sampleDto = CurrencyDto.builder()
            .ccy("USD")
            .rate(new BigDecimal("12345"))
            .build();

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getTodayCurrency_shouldReturnListOfCurrencies() throws Exception {
        Mockito.when(currencyService.getCurrency(null)).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/currency"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].Ccy", is("USD")))
                .andExpect(jsonPath("$[0].Rate", is(12345)
                ));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCurrencyByDate_shouldReturnListOfCurrencies() throws Exception {
        LocalDate date = LocalDate.of(2023, 12, 25);
        Mockito.when(currencyService.getCurrency(date)).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/currency/{date}", date))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].Ccy", is("USD")))
                .andExpect(jsonPath("$[0].Rate", is(12345)));
    }


    @Test
    void getCurrency_shouldReturnUnauthorizedIfAnonymous() throws Exception {
        mockMvc.perform(get("/api/currency"))
                .andExpect(status().isUnauthorized());
    }
}
