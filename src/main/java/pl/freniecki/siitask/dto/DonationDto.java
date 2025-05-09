package pl.freniecki.siitask.dto;

import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;

public record DonationDto(Currency currency, BigDecimal value) {}
