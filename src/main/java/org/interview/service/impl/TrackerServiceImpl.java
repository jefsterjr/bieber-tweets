package org.interview.service.impl;

import com.google.api.client.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.interview.config.TwitterConfiguration;
import org.interview.config.oauth.TwitterAuthenticationException;
import org.interview.service.ProcessorService;
import org.interview.service.TrackerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class TrackerServiceImpl implements TrackerService {

    @Value("${text-track}")
    private String trackText;

    private final TwitterConfiguration configuration;
    private final ProcessorService processorService;

    public TrackerServiceImpl(TwitterConfiguration configuration, ProcessorService processorService) {
        this.configuration = configuration;
        this.processorService = processorService;
    }

    @Override
    public void trackTweets() throws IOException, TwitterAuthenticationException {

        log.info("Initializing tweet track with filter: '{}'", trackText);
        HttpResponse response = configuration.getHttpResponse(trackText);

        try {
            processorService.getTweets(response.getContent());
        } catch (IOException e) {
            log.error("Error while processing the stream", e);
        } finally {
            response.disconnect();
        }

    }


}
