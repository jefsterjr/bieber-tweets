package org.interview;

import lombok.extern.slf4j.Slf4j;
import org.interview.service.impl.TrackerServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class BiebertweetsApplication implements CommandLineRunner {

    private final TrackerServiceImpl service;

    public BiebertweetsApplication(TrackerServiceImpl service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(BiebertweetsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing tracking service");
        service.trackTweets();
    }
}
