package org.interview.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.interview.exception.TwitterAuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Provide access to the Twitter API by implementing the required OAuth2 flowa
 */
@Slf4j
@Component
public class TwitterAuthenticator {
    private final String consumerKey;
    private final String consumerSecret;
    private String bearerToken;
    private final String oauth2TokenUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TwitterAuthenticator(@Value("${twitter.consumer-key}") String consumerKey,
                                @Value("${twitter.consumer-secret}") String consumerSecret,
                                @Value("${twitter.oauth2.url}") String oauth2TokenUrl) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.oauth2TokenUrl = oauth2TokenUrl;
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get the bearer token
     *
     * @return The bearer token
     */
    public String getBearerToken() throws TwitterAuthenticationException {
        if (bearerToken == null) {
            bearerToken = requestAccessToken();
        }
        return bearerToken;
    }

    /**
     * Create a request to Twitter OAUTH2 api for getting the token
     *
     * @return The access token
     */
    private String requestAccessToken() throws TwitterAuthenticationException {
        String s = encodeCredentialsToBase64(consumerKey, consumerSecret);
        String authorizationHeader = "Basic " + s;
        String[] headers = {"Authorization", authorizationHeader, "Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"};
        String requestBody = "grant_type=client_credentials";

        URI uri = URI.create(oauth2TokenUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).headers(headers).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TwitterAuthenticationException(e);
        }
        return getTokenFromBody(response.body());

    }

    /**
     * Return credentials in base64
     *
     * @param consumerKey    The application consumer key
     * @param consumerSecret The application consumer secret
     * @return String with encoded credentials
     */
    private String encodeCredentialsToBase64(String consumerKey, String consumerSecret) {
        String credentials = consumerKey + ":" + consumerSecret;
        byte[] encodedCredentials = Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8));
        return new String(encodedCredentials, StandardCharsets.UTF_8);
    }

    /**
     * Return access_token taken from response
     *
     * @param responseBody The OAUTH2 token response body
     * @return String with the access_token
     */
    private String getTokenFromBody(String responseBody) throws TwitterAuthenticationException {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode.get("access_token").asText();
        } catch (Exception e) {
            throw new TwitterAuthenticationException("Unable to retrieve the access_token from response: " + e.getMessage(), e);
        }

    }
}
