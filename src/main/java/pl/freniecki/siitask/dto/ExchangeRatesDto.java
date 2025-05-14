package pl.freniecki.siitask.dto;

import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;
import java.util.Map;

public record ExchangeRatesDto(String base, Map<Currency, BigDecimal> rates) {}
