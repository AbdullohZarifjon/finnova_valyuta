package com.example.finnovavalyutatask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinnovaValyutaTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinnovaValyutaTaskApplication.class, args);
    }

}
