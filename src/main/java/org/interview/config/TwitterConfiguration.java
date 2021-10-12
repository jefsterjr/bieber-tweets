package org.interview.config;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.interview.config.oauth.TwitterAuthenticationException;
import org.interview.config.oauth.TwitterAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class TwitterConfiguration {

    @Value("${twitter-filter-url}")
    private String twitterFilterUrl;

    @Value("${consumer-key}")
    private String consumerKey;

    @Value("${consumer-secret}")
    private String consumerSecret;

    public HttpResponse getHttpResponse(String trackText) throws TwitterAuthenticationException, IOException {

        log.info("Build request and get the response");
        return getHttpRequestFactory().buildGetRequest(buildUrl(trackText)).execute();
    }

    private HttpRequestFactory getHttpRequestFactory() throws TwitterAuthenticationException {

        log.info("Authenticate on Twitter API");
        return new TwitterAuthenticator(System.out, consumerKey, consumerSecret).getAuthorizedHttpRequestFactory();
    }

    private GenericUrl buildUrl(String trackText) {

        log.info("Build URL");
        return new GenericUrl(twitterFilterUrl).set("track", trackText);
    }
}
