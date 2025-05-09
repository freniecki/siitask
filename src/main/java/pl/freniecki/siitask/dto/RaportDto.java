package pl.freniecki.siitask.dto;

import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;

public record RaportDto(String eventName, BigDecimal amount, Currency currency) {}
