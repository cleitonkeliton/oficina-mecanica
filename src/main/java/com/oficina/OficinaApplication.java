package com.oficina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class OficinaApplication {

    public static void main(String[] args) {
        // Horários no fuso da oficina, mesmo que o servidor esteja em outro (ex.: UTC na nuvem)
        String fuso = System.getenv().getOrDefault("OFICINA_FUSO", "America/Sao_Paulo");
        TimeZone.setDefault(TimeZone.getTimeZone(fuso));
        SpringApplication.run(OficinaApplication.class, args);
    }
}
