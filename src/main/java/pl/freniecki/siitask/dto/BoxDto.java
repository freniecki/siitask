package pl.freniecki.siitask.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoxDto {
    private boolean isAssigned;
    private boolean isEmpty;
}
