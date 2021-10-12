package org.interview.service;

import org.interview.config.oauth.TwitterAuthenticationException;

import java.io.IOException;

public interface TrackerService {
    /**
     * Service method to track twitter information
     */
    void trackTweets() throws IOException, TwitterAuthenticationException;
}
