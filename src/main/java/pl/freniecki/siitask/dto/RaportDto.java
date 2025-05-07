package pl.freniecki.siitask.dto;

import lombok.Data;
import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;

@Data
public class RaportDto {
    private String eventName;
    private BigDecimal amount;
    private Currency currency;
}
