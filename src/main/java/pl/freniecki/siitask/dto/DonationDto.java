package pl.freniecki.siitask.dto;

import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public class DonationDto {
    UUID boxId;
    Currency currency;
    BigDecimal value;
}
