package org.interview.service;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.AddOrDeleteRulesRequest;
import com.twitter.clientlib.model.AddOrDeleteRulesResponse;
import com.twitter.clientlib.model.AddRulesRequest;
import com.twitter.clientlib.model.Problem;
import com.twitter.clientlib.model.Rule;
import com.twitter.clientlib.model.RuleNoId;
import com.twitter.clientlib.model.RulesLookupResponse;
import org.interview.configuration.TwitterAuthenticator;
import org.interview.exception.TwitterApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwitterApiServiceUnitTest {

    @Mock
    private TwitterApi apiInstance;

    @Mock
    private TweetsApi tweetsApi;

    @Mock
    private TweetsApi.APIgetRulesRequest rulesRequest;

    @Mock
    private TweetsApi.APIaddOrDeleteRulesRequest addOrDeleteRulesRequest;

    @Mock
    private TwitterAuthenticator authenticator;

    @Mock
    private RulesLookupResponse rulesLookupResponse;
    @Mock
    private TweetsApi.APIsearchStreamRequest streamRequest;

    @InjectMocks
    private TwitterApiService twitterApiService;

    private static final String SEARCH_EXP = "\"bieber\"";
    private static final String SEARCH_TEXT = "bieber";

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        mockApiInstance();
    }

    @Test
    void addNewRule_Success() throws TwitterApiException, ApiException {

        mockRuleDoesNotExist();

        when(apiInstance.tweets()).thenReturn(tweetsApi);
        when(tweetsApi.addOrDeleteRules(any())).thenReturn(addOrDeleteRulesRequest);
        when(addOrDeleteRulesRequest.execute()).thenReturn(new AddOrDeleteRulesResponse());


        twitterApiService.addRule(SEARCH_TEXT);

        RuleNoId expectedRule = new RuleNoId();
        expectedRule.setValue(SEARCH_EXP);
        expectedRule.setTag("bieber-java-exercise");
        AddRulesRequest expectedRequest = new AddRulesRequest();
        expectedRequest.add(Collections.singletonList(expectedRule));
        AddOrDeleteRulesRequest expectedAddOrDeleteRequest = new AddOrDeleteRulesRequest(expectedRequest);
        verify(apiInstance.tweets()).addOrDeleteRules(expectedAddOrDeleteRequest);
    }

    @Test
    void addNewRule_ErrorOnAdding() throws ApiException {

        mockRuleDoesNotExist();
        when(apiInstance.tweets()).thenReturn(tweetsApi);
        when(tweetsApi.addOrDeleteRules(any())).thenReturn(addOrDeleteRulesRequest);
        mockAddOrDeleteCallWithError();
        assertThrows(TwitterApiException.class, () -> {
            twitterApiService.addRule(SEARCH_TEXT);
        });
    }


    @Test
    void addNewRule_ExceptionOnAdding() throws ApiException {
        when(apiInstance.tweets()).thenReturn(tweetsApi);
        when(tweetsApi.getRules()).thenReturn(rulesRequest);
        when(rulesRequest.execute()).thenThrow(new ApiException());
        assertThrows(TwitterApiException.class, () -> {
            twitterApiService.addRule(SEARCH_TEXT);
        });
    }

    private void mockAddOrDeleteCallWithError() throws ApiException {
        AddOrDeleteRulesResponse response = new AddOrDeleteRulesResponse();
        Problem problem = new Problem();
        problem.setStatus(429);
        problem.setTitle("Too many requests");
        response.addErrorsItem(problem);
        when(addOrDeleteRulesRequest.execute()).thenReturn(response);
    }


    @Test
    void testAddRule_RuleAlreadyExists() throws TwitterApiException, ApiException {
        when(apiInstance.tweets()).thenReturn(tweetsApi);
        mockRuleExists();
        twitterApiService.addRule(SEARCH_TEXT);
        Mockito.verifyNoInteractions(addOrDeleteRulesRequest);
    }

    @Test
    void openSearchStream_success() throws Exception {
        TwitterCredentialsBearer credentialsBearerMock = mock(TwitterCredentialsBearer.class);
        when(apiInstance.tweets()).thenReturn(tweetsApi);
        // Given
        when(tweetsApi.searchStream()).thenReturn(streamRequest);
        when(streamRequest.tweetFields(any())).thenReturn(streamRequest);
        when(streamRequest.userFields(any())).thenReturn(streamRequest);
        when(streamRequest.expansions(any())).thenReturn(streamRequest);
        when(streamRequest.endTime(any())).thenReturn(streamRequest);
        when(streamRequest.execute(anyInt())).thenReturn(loadPayloadFromFile());

        InputStream stream = twitterApiService.openSearchStream();

        Assertions.assertEquals(Arrays.toString(stream.readAllBytes()), Arrays.toString(loadPayloadFromFile().readAllBytes()));
        verify(authenticator).getBearerToken();
        verify(tweetsApi).searchStream();
        verify(streamRequest).tweetFields(any());
        verify(streamRequest).userFields(any());
        verify(streamRequest).expansions(any());
        verify(streamRequest).execute(anyInt());
    }

    @Test
    void openSearchStream_ErrorApiException() throws Exception {
        TwitterCredentialsBearer credentialsBearerMock = mock(TwitterCredentialsBearer.class);
        when(apiInstance.tweets()).thenReturn(tweetsApi);
        // Given
        when(tweetsApi.searchStream()).thenReturn(streamRequest);
        when(streamRequest.tweetFields(any())).thenReturn(streamRequest);
        when(streamRequest.userFields(any())).thenReturn(streamRequest);
        when(streamRequest.expansions(any())).thenReturn(streamRequest);
        when(streamRequest.endTime(any())).thenReturn(streamRequest);
        when(streamRequest.execute(anyInt())).thenThrow(new ApiException());

        assertThrows(TwitterApiException.class, () -> {
            twitterApiService.openSearchStream();
        });

        // Then
        verify(authenticator).getBearerToken();
        verify(tweetsApi).searchStream();
        verify(streamRequest).tweetFields(any());
        verify(streamRequest).userFields(any());
        verify(streamRequest).expansions(any());
        verify(streamRequest).execute(anyInt());
    }

    private void mockRuleExists() throws ApiException {
        when(tweetsApi.getRules()).thenReturn(rulesRequest);
        when(rulesRequest.execute()).thenReturn(rulesLookupResponse);
        List<Rule> rules = new ArrayList<>();
        Rule rule = new Rule();
        rule.setValue(SEARCH_EXP);
        rule.setTag("bieber-java-exercise");
        rules.add(rule);
        when(rulesLookupResponse.getData()).thenReturn(rules);
    }

    private void mockRuleDoesNotExist() throws ApiException {
        when(tweetsApi.getRules()).thenReturn(rulesRequest);
        when(rulesRequest.execute()).thenReturn(rulesLookupResponse);
        when(rulesLookupResponse.getData()).thenReturn(null);
    }

    private void mockApiInstance() throws NoSuchFieldException, IllegalAccessException {
        Field apiInstanceField = TwitterApiService.class.getDeclaredField("apiInstance");
        apiInstanceField.setAccessible(true);
        apiInstanceField.set(twitterApiService, apiInstance);
    }

    private InputStream loadPayloadFromFile() throws IOException {
        return new ByteArrayInputStream(getClass().getResourceAsStream("/valid_tweet.json").readAllBytes());
    }
}
