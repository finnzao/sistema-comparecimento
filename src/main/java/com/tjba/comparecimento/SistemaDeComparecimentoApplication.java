package com.tjba.comparecimento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Sistema de Controle de Comparecimento para pessoas em liberdade provisória.
 */
@SpringBootApplication
@EnableJpaAuditing
public class SistemaDeComparecimentoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaDeComparecimentoApplication.class, args);
    }
}