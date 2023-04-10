package org.interview;

import lombok.extern.slf4j.Slf4j;
import org.interview.exception.TwitterApiException;
import org.interview.service.TwitterStreamingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
@Slf4j
public class JavaExerciseApplication implements CommandLineRunner {
    private final TwitterStreamingService streamingService;

    public JavaExerciseApplication(TwitterStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(JavaExerciseApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            streamingService.readTweetsStreaming();
            System.exit(200);
        } catch (TwitterApiException | IOException e) {
            System.exit(400);
        }

    }
}
