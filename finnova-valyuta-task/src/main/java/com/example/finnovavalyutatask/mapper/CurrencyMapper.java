package com.example.finnovavalyutatask.mapper;

import com.example.finnovavalyutatask.payload.dto.CurrencyDto;
import com.example.finnovavalyutatask.entity.CurrencyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {


    @Mappings({
            @Mapping(source = "ccy", target = "currencyCode"),
            @Mapping(source = "ccyNmUz", target = "nameUz"),
            @Mapping(source = "ccyNmRu", target = "nameRu"),
            @Mapping(source = "ccyNmEn", target = "nameEn"),
            @Mapping(source = "diff", target = "difference"),
            @Mapping(source = "rate", target = "rate"),
            @Mapping(source = "code", target = "code"),
            @Mapping(source = "date", target = "date", qualifiedByName = "stringToLocalDate")
    })
    CurrencyEntity toEntity(CurrencyDto dto);

    @Named("stringToLocalDate")
    static LocalDate stringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(date, formatter);
    }
}
