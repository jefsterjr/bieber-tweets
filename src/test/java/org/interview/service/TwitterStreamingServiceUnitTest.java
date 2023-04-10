package org.interview.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.extern.slf4j.Slf4j;
import org.interview.exception.TwitterApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class TwitterStreamingServiceUnitTest {
    @Mock
    private TwitterApiService twitterApiService;

    @InjectMocks
    private TwitterStreamingService twitterStreamingService;


    @Test
    void testReadTweetsStreaming_success() throws TwitterApiException, IOException {
        when(twitterApiService.openSearchStream()).thenReturn(loadPayloadFromFile());
        doNothing().when(twitterApiService).addRule(anyString());
        twitterStreamingService.readTweetsStreaming();
        verify(twitterApiService, times(1)).openSearchStream();
        verify(twitterApiService, times(1)).addRule(anyString());
    }

    @Test
    void readTweetsStreaming_throwsTwitterApiException_whenAddRuleFails() throws Exception {
        doThrow(new TwitterApiException("Add rule failed")).when(twitterApiService).addRule("bieber");
        assertThrows(TwitterApiException.class, () -> {
            twitterStreamingService.readTweetsStreaming();
        });
    }

    @Test
    void readTweetsStreaming_logsError_whenIOExceptionOccurs() throws Exception {
        String payload = "invalid_payload";
        when(twitterApiService.openSearchStream()).thenReturn(new ByteArrayInputStream(payload.getBytes()));
        assertThrows(IOException.class, () -> {
            twitterStreamingService.readTweetsStreaming();
        });
    }

    @Test
    void openStreaming_checkLogs() throws Exception {

        when(twitterApiService.openSearchStream()).thenReturn(loadPayloadFromFile());
        Logger logger = LoggerFactory.getLogger(TwitterStreamingService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        ((ch.qos.logback.classic.Logger) logger).addAppender(listAppender);

        twitterStreamingService.readTweetsStreaming();

        List<ILoggingEvent> logsList = listAppender.list;

        String expectedLog = """
                Tweet\s
                Id: 1643806880760754177\s
                Creation date: 2023-04-06T02:44:35Z[UTC]\s
                Text: blablabla bieber
                -------
                User\s
                Id: 881584770211295232\s
                Creation date: 2023-02-02T18:46:03Z[UTC]\s
                Name: noe.\s
                Screen Name: twitterUser123
                
                """;
        System.out.print(expectedLog);
        String format = new DecimalFormat("#.##").format(0.03);

        assertEquals(expectedLog, logsList.get(0).getFormattedMessage());
        assertEquals("Total messages received: 1", logsList.get(1).getFormattedMessage());
        assertEquals("Messages per second: " + format, logsList.get(2).getFormattedMessage());

        listAppender.stop();
        ((ch.qos.logback.classic.Logger) logger).detachAppender(listAppender);
    }

    private InputStream loadPayloadFromFile() throws IOException {
        return new ByteArrayInputStream(getClass().getResourceAsStream("/valid_tweet.json").readAllBytes());
    }
}