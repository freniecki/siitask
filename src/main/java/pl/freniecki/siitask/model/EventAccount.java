package pl.freniecki.siitask.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EventAccount {
    private UUID id = UUID.randomUUID();
    private UUID eventId;

    private final Currency currency;
    private BigDecimal vault;
}
