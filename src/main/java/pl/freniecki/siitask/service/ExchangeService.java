package pl.freniecki.siitask.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ExchangeService {
    private static final Logger log = Logger.getLogger(ExchangeService.class.getName());

    /**
     * Uses external service to fetch currency exchange rates.
     */
    private final ExternalExchangeRatesService externalExchangeRatesService;

    /**
     * Currency exchange rates used for operations.
     */
    private Map<Currency, BigDecimal> rates;

    /**
     * Static backup in case Open Exchange Rates API is unavailable
     */
    private final Map<Currency, BigDecimal> staticRate = Map.of(
            Currency.USD, new BigDecimal("1.0"),
            Currency.EUR, new BigDecimal("0.9"),
            Currency.GBP, new BigDecimal("0.8")
    );

    public ExchangeService(ExternalExchangeRatesService externalExchangeRatesService) {
        this.externalExchangeRatesService = externalExchangeRatesService;
    }

    /**
     * Initialize exchange rates. If fetching from Open Exchange Rates API fails, use static backup.
     */
    @PostConstruct
    public void init() {
        Map<Currency, BigDecimal> exchangeRates;
        exchangeRates = externalExchangeRatesService.getExchangeRates();

        if (exchangeRates.isEmpty()) {
            log.info("Failed to fetch Open Exchange Rates API. Empty response. Using static backup.");
            rates = staticRate;
        } else {
            log.info("Fetched exchange rates: " + exchangeRates);
            exchangeRates.put(Currency.USD, BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP));
            rates = exchangeRates;
        }
    }

    // ================== OPERATIONS ==================

    /**
     * Calculate sum of all currencies in vault. Simply checks if the map is empty.
     * @param vault Map of currencies with values from given Box.
     * @return Sum of all currencies
     */
    public BigDecimal getAbsoluteSum(Map<Currency, BigDecimal> vault) {
        return vault.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates sum of all currencies in vault, based on requested currency. Uses rates to convert currencies other than requested.
     * @param vault Map of all currencies with values
     * @param requestedCurrency Currency for which sum should be calculated
     * @return Sum of all converted currencies
     */
    public BigDecimal getConvertedSum(Map<Currency, BigDecimal> vault, Currency requestedCurrency) {
        log.info("requested currency: " + requestedCurrency);

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
            if (rates.containsKey(entry.getKey())) {
                BigDecimal exchangeRate = rates.get(requestedCurrency).divide(rates.get(currentCurrency), RoundingMode.HALF_UP);
                BigDecimal convertedValue = currentValue.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
                log.info("Exchange rate: " + exchangeRate + " -> converted value: " + convertedValue);

                sum = sum.add(convertedValue);
                log.info("New sum: " + sum);
            }
        }
        log.info("Final sum: " + sum);
        return sum;
    }

}
