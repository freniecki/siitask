package pl.freniecki.siitask.dto;

import lombok.Builder;
import lombok.Data;
import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;

@Data
@Builder
public class DonationDto {
    Currency currency;
    BigDecimal value;
}
