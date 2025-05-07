package pl.freniecki.siitask.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferDto {
    private UUID boxId;
    private BigDecimal value;
}
