package pl.freniecki.siitask.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class SecretConfig {
    private final Logger log = Logger.getLogger(SecretConfig.class.getName());
    private final Properties properties = new Properties();

    public SecretConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("secret.properties")) {
            if (input == null) {
                log.warning("File secret.properties not found in resources.");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            log.warning("Error while loading secret.properties: " + e.getMessage());
        }
    }

    public String getApiKey() {
        return properties.getProperty("openexchangerates.api.key");
    }
}
