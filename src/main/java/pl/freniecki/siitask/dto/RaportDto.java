package pl.freniecki.siitask.dto;

import lombok.Data;
import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RaportDto {
    private UUID eventId;
    private BigDecimal amount;
    private Currency currency;
}
