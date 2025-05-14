package pl.freniecki.siitask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:secret.properties", ignoreResourceNotFound = true)
public class SiitaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiitaskApplication.class, args);
	}

}
