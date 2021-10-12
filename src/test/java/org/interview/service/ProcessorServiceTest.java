package org.interview.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.interview.model.Tweet;
import org.interview.model.User;
import org.interview.service.impl.FileExportServiceImpl;
import org.interview.service.impl.ProcessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

public class ProcessorServiceTest {

    ProcessorService processorService;

    FileExportService fileExportService;

    private Gson gson;

    @BeforeEach
    void setUp() {
        fileExportService = Mockito.mock(FileExportServiceImpl.class);
        processorService = new ProcessorServiceImpl(fileExportService);
        gson = new GsonBuilder().setDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy").create();
    }


    @Test
    void getTweets() throws IOException {
        Tweet tweet = new Tweet(getRandomString(), new Date(), getRandomString(),
                new User(getRandomString(),
                        new Date(),
                        getRandomString(),
                        getRandomString()));

        processorService.getTweets(new ByteArrayInputStream(gson.toJson(tweet).getBytes(StandardCharsets.UTF_8)));
        doNothing().when(fileExportService).createJsonTweetFile(any(), any());
        doNothing().when(fileExportService).createJsonStatisticFile(any(), any());
    }

    @Test
    void getTweets_exceed_tweet_limit() throws IOException {
        List<Tweet> list = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            Tweet tweet = new Tweet(getRandomString(), new Date(), getRandomString(),
                    new User(getRandomString(),
                            new Date(),
                            getRandomString(),
                            getRandomString()));
            list.add(tweet);
        }

        processorService.getTweets(new ByteArrayInputStream(gson.toJson(list).getBytes(StandardCharsets.UTF_8)));
        doNothing().when(fileExportService).createJsonTweetFile(any(), any());
        doNothing().when(fileExportService).createJsonStatisticFile(any(), any());
    }

    private String getRandomString() {
        return String.valueOf(new Random().nextInt());
    }
}
