package pl.freniecki.siitask.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.freniecki.siitask.dto.ExchangeRatesDto;
import pl.freniecki.siitask.model.Currency;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ExternalExchangeRatesService {
    private final Logger log = Logger.getLogger(ExternalExchangeRatesService.class.getName());
    private final RestTemplate restTemplate;

    public ExternalExchangeRatesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    String apiBaseUrl = "https://openexchangerates.org/api/latest.json";
    String currencyBase = "DLR";
    String currencySymbols = "EUR,GBP";

    public Map<Currency, BigDecimal> getExchangeRates() {
        String url = String.format("%s?app_id=%s&base=%s&symbols=%s", apiBaseUrl, "0000", currencyBase, currencySymbols);
        log.info("Fetching exchange rates from: " + url);

        ExchangeRatesDto response = restTemplate.getForObject(url, ExchangeRatesDto.class);
        if (response == null) {
            log.warning("Failed to fetch Open Exchange Rates API.");
            return Map.of();
        }

        if (!validateRates(response.rates())) {
            return Map.of();
        }

        return response.rates();
    }

    private boolean validateRates(Map<Currency, BigDecimal> exchangeRates) {
        for (Map.Entry<Currency, BigDecimal> entry : exchangeRates.entrySet()) {
            Currency currency = entry.getKey();
            BigDecimal rate = entry.getValue();

            if (!EnumSet.allOf(Currency.class).contains(currency)) {
                log.warning("Invalid currency: " + currency);
                return false;
            }

            if (rate.compareTo(BigDecimal.ZERO) <= 0) {
                log.warning("Invalid rate: " + rate + " for currency: " + currency);
                return false;
            }
        }
        return true;
    }
}

