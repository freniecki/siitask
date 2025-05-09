package pl.freniecki.siitask.service;

import org.springframework.stereotype.Service;
import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ExchangeService {
    public BigDecimal getSum(Map<Currency, BigDecimal> vault) {
        return BigDecimal.ZERO;
    }
}
