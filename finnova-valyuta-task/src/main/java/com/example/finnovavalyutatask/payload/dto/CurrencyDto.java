package com.example.finnovavalyutatask.payload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CurrencyDto{
    @JsonProperty("id")
    Integer id;

    @JsonProperty("Code")
    String code;

    @JsonProperty("Ccy")
    String ccy;

    @JsonProperty("CcyNm_RU")
    String ccyNmRu;

    @JsonProperty("CcyNm_UZ")
    String ccyNmUz;

    @JsonProperty("CcyNm_UZC")
    String ccyNmUzc;

    @JsonProperty("CcyNm_EN")
    String ccyNmEn;

    @JsonProperty("Nominal")
    String nominal;

    @JsonProperty("Rate")
    BigDecimal rate;

    @JsonProperty("Diff")
    BigDecimal diff;

    @JsonProperty("Date")
    String date;

}