package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.config.SecurityConfig;
import com.example.finnovavalyutatask.payload.dto.CurrencyDto;
import com.example.finnovavalyutatask.service.CurrencyService;
import com.example.finnovavalyutatask.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private ObjectMapper objectMapper;

    private final CurrencyDto sampleDto = CurrencyDto.builder()
            .ccy("USD")
            .rate(new BigDecimal("12345"))
            .build();

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Bugungi valyutalarni olish – muvaffaqiyatli")
    void getTodayCurrency_shouldReturnListOfCurrencies() throws Exception {
        Mockito.when(currencyService.getCurrency(null)).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/currency"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Bugungi valyutalar ro'yxati")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].Ccy", is("USD")))
                .andExpect(jsonPath("$.data[0].Rate", is(12345)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Sana bo‘yicha valyutalarni olish – muvaffaqiyatli")
    void getCurrencyByDate_shouldReturnListOfCurrencies() throws Exception {
        LocalDate date = LocalDate.of(2023, 12, 25);
        Mockito.when(currencyService.getCurrency(date)).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/currency/{date}", date))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Valyutalar ro'yxati")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].Ccy", is("USD")))
                .andExpect(jsonPath("$.data[0].Rate", is(12345)));
    }

    @Test
    @DisplayName("Valyutalar – foydalanuvchi authenticated emas")
    void getCurrency_shouldReturnUnauthorizedIfAnonymous() throws Exception {
        mockMvc.perform(get("/api/currency"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getTodayCurrency_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/currency"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTodayCurrency_shouldReturnUnauthorizedIfNoToken() throws Exception {
        mockMvc.perform(get("/api/currency"))
                .andExpect(status().isUnauthorized());
    }
}
