package org.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.interview.exception.TwitterApiException;
import org.interview.model.Includes;
import org.interview.model.Tweet;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwitterStreamingService {
    private static final int MAX_MESSAGES = 100;
    private static final int MAX_TIME_SECONDS = 30;
    private static final String SEARCH_TEXT = "bieber";
    private int totalMessagesCounter = 0;
    private final TwitterApiService twitterApiService;


    public void readTweetsStreaming() throws TwitterApiException, IOException {
        twitterApiService.addRule(SEARCH_TEXT);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(twitterApiService.openSearchStream()))) {
            String line;
            long init = System.currentTimeMillis();
            while ((line = in.readLine()) != null && totalMessagesCounter < MAX_MESSAGES && timeLimitCheck(init)) {
                if (!line.isEmpty()) {
                    Tweet tweet = readValue(line);
                    if (tweet.matchingRules().stream().anyMatch(matchingRules -> matchingRules.tag().equals("bieber-java-exercise"))) {
                        Includes.User user = tweet.includes().users().get(0);
                        displayResult(tweet, user);
                        totalMessagesCounter++;
                    }
                }
            }
            stopStreaming(in);
        }
    }

    private void stopStreaming(BufferedReader in) throws IOException {
        in.close();
        String format = new DecimalFormat("#.##").format((double) totalMessagesCounter / MAX_TIME_SECONDS);
        log.info("Total messages received: {}", totalMessagesCounter);
        log.info("Messages per second: {}", format);
    }

    private static void displayResult(Tweet tweet, Includes.User user) {
        log.info("""
                Tweet\s
                Id: {}\s
                Creation date: {}\s
                Text: {}
                -------
                User\s
                Id: {}\s
                Creation date: {}\s
                Name: {}\s
                Screen Name: {}

                """, tweet.data().id(), tweet.data().createdAt(), tweet.data().text(), tweet.data().authorId(), user.createdAt(), user.name(), user.username());
    }

    /**
     * Read value from JSON string to specified type using ObjectMapper
     *
     * @param json The JSON string
     * @return The deserialized object of type T
     * @throws IOException In case of IO errors
     */
    private Tweet readValue(String json) throws IOException {
        return new ObjectMapper().findAndRegisterModules().readValue(json, Tweet.class);
    }

    private boolean timeLimitCheck(long startTime) {
        return ((System.currentTimeMillis() - startTime) / 1000L) < 30;
    }
}
