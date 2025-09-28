package com.gamestack.games;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Classe principale de l'application.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.gamestack.games.repository")
public class GamesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamesServiceApplication.class, args);
    }
}
