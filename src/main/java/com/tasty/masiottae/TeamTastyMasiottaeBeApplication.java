package com.tasty.masiottae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TeamTastyMasiottaeBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamTastyMasiottaeBeApplication.class, args);
    }

}
