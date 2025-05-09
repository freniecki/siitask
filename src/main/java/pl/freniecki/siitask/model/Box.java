package pl.freniecki.siitask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Box {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;
    private Long eventId;

    @ElementCollection
    @CollectionTable(name = "currency_balance", joinColumns = @JoinColumn(name = "box_id"))
    @MapKeyColumn(name = "currency")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "amount")
    private Map<Currency, BigDecimal> vault;

    public void emptyVault() {
        if (vault != null) {
            vault.clear();
        }
    }
}
