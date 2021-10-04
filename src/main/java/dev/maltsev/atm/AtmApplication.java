package dev.maltsev.atm;

import dev.maltsev.atm.controller.AtmController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class AtmApplication implements CommandLineRunner {

    @Autowired
    AtmController controller;

    public static void main(String[] args) {
        SpringApplication.run(AtmApplication.class, args);
    }

    @Override
    public void run(String... args) {
        controller.doProcess(System.in, System.out);
    }
}
