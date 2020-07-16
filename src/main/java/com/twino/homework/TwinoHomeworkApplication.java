package com.twino.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TwinoHomeworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwinoHomeworkApplication.class, args);
    }

}
