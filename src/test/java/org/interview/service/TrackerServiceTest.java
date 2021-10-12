package org.interview.service;

import com.google.api.client.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.assertj.core.api.Assertions;
import org.interview.config.TwitterConfiguration;
import org.interview.config.oauth.TwitterAuthenticationException;
import org.interview.model.Tweet;
import org.interview.model.User;
import org.interview.service.impl.TrackerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.AssertionErrors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class
TrackerServiceTest {


    TrackerService trackerService;

    @Mock
    private TwitterConfiguration configuration;

    @Mock
    private ProcessorService processorService;

    @Mock
    private HttpResponse httpResponse;

    private Gson gson;

    @BeforeEach
    void setUp() {
        trackerService = new TrackerServiceImpl(configuration, processorService);
        gson = new GsonBuilder().setDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy").create();
    }

    @Test
    public void trackTweetsTest() throws TwitterAuthenticationException, IOException {

        Tweet tweet = new Tweet(getRandomString(), new Date(), getRandomString(),
                new User(getRandomString(),
                        new Date(),
                        getRandomString(),
                        getRandomString()));
        Mockito.when(configuration.getHttpResponse(null) ).thenReturn(httpResponse);
        Mockito.when(httpResponse.getContent()).thenReturn(new ByteArrayInputStream(gson.toJson(tweet).getBytes(StandardCharsets.UTF_8)));
        trackerService.trackTweets();
    }

    @Test
    public void trackTweetsTest_IOException() throws TwitterAuthenticationException, IOException {

        Mockito.when(configuration.getHttpResponse(null)).thenReturn(httpResponse);
        Mockito.when(httpResponse.getContent()).thenThrow(new IOException());
        try {
            trackerService.trackTweets();
        } catch (Exception e) {
            AssertionErrors.assertEquals("IOException", e, Assertions.assertThatIOException());
        }
    }

    private String getRandomString() {
        return String.valueOf(new Random().nextInt());
    }
}
