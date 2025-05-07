package pl.freniecki.siitask.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Event {
    private final UUID id = UUID.randomUUID();
    private String name;

    private final UUID accountId;
}
