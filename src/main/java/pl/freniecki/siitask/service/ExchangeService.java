package pl.freniecki.siitask.service;

import org.springframework.stereotype.Service;
import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ExchangeService {
    private static final Logger log = Logger.getLogger(ExchangeService.class.getName());

    private final Map<Currency, BigDecimal> dollarRate = Map.of(
            Currency.DLR, new BigDecimal("1.0"),
            Currency.EUR, new BigDecimal("0.9"),
            Currency.GBP, new BigDecimal("0.8")
    );

    public BigDecimal getAbsoluteSum(Map<Currency, BigDecimal> vault) {
        return vault.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getConvertedSum(Map<Currency, BigDecimal> vault, Currency requestedCurrency) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<Currency, BigDecimal> entry : vault.entrySet()) {
            Currency currentCurrency = entry.getKey();
            BigDecimal currentValue = entry.getValue();
            log.info("Value: " + currentValue + " " + currentCurrency);

            // the same currency as requested
            if (currentCurrency.equals(requestedCurrency)) {
                sum = sum.add(currentValue);
                log.info("Same currency. New sum: " + sum);
            }

            // currency different from requested
            if (dollarRate.containsKey(entry.getKey())) {
                BigDecimal exchangeRate = dollarRate.get(requestedCurrency).divide(dollarRate.get(currentCurrency), RoundingMode.HALF_UP);
                log.info("Exchange rate: " + exchangeRate);

                BigDecimal convertedValue = currentValue.multiply(exchangeRate);
                log.info("Converted value: " + convertedValue);

                sum = sum.add(convertedValue);
                log.info("New sum: " + sum);
            }
        }
        return sum;
    }

}
