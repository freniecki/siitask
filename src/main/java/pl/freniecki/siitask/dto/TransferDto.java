package pl.freniecki.siitask.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferDto(UUID boxId, BigDecimal value) {}
