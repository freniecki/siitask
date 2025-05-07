package pl.freniecki.siitask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Box {
    private final UUID id = UUID.randomUUID();
    private UUID eventId;

    // storage for money
    private Map<Currency, BigDecimal> vault;
}
