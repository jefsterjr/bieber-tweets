package org.interview.service;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.AddOrDeleteRulesRequest;
import com.twitter.clientlib.model.AddOrDeleteRulesResponse;
import com.twitter.clientlib.model.AddRulesRequest;
import com.twitter.clientlib.model.RuleNoId;
import com.twitter.clientlib.model.RulesLookupResponse;
import org.interview.configuration.TwitterAuthenticator;
import org.interview.exception.TwitterApiException;
import org.interview.exception.TwitterAuthenticationException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class TwitterApiService {

    private final TwitterApi apiInstance;
    private static final String RULE_TAG = "bieber-java-exercise";


    public TwitterApiService(TwitterAuthenticator authenticator) throws TwitterApiException {
        this.apiInstance = getTwitterApiInstance(authenticator);
    }

    /**
     * Add a rule to the streaming
     *
     * @param seachText
     * @throws TwitterApiException
     */
    public void addRule(String seachText) throws TwitterApiException {
        String searchExpression = "\"" + seachText + "\"";
        try {
            if (!ruleExists()) {
                RuleNoId ruleNoId = new RuleNoId();
                ruleNoId.setValue(searchExpression);
                ruleNoId.setTag(RULE_TAG);
                AddRulesRequest addRulesRequest = new AddRulesRequest();
                addRulesRequest.add(Collections.singletonList(ruleNoId));
                AddOrDeleteRulesRequest request = new AddOrDeleteRulesRequest(addRulesRequest);
                AddOrDeleteRulesResponse response = apiInstance.tweets().addOrDeleteRules(request).execute();
                if (response.getErrors() != null) {
                    throw new TwitterApiException("Error on adding the Rule.");
                }
            }
        } catch (ApiException e) {
            throw new TwitterApiException("Error on adding the Rule", e);
        }
    }

    /**
     * Verify if the rule exists
     *
     * @return if the rule exists
     * @throws ApiException
     */
    private boolean ruleExists() throws ApiException {
        RulesLookupResponse response = apiInstance.tweets().getRules().execute();
        if (response.getData() != null) {
            return response.getData().stream().anyMatch(rule -> Objects.equals(rule.getTag(), RULE_TAG));
        }
        return false;

    }


    /**
     * Init the search stream on Twitter API
     *
     * @return Stream of data
     * @throws TwitterApiException
     */
    public InputStream openSearchStream() throws TwitterApiException {
        try {
            return apiInstance.tweets().searchStream()
                    .tweetFields(getTweetFields())
                    .userFields(getUserFields())
                    .expansions(new HashSet<>(List.of("author_id")))
                    .endTime(OffsetDateTime.now().plus(30, ChronoUnit.SECONDS))
                    .execute(4);
        } catch (ApiException e) {
            throw new TwitterApiException("Error on open search stream", e);
        }
    }

    /**
     * Creating a Twitter API instance using a OAuth 2 Token
     *
     * @param authenticator
     * @return Twitter Api instance
     * @throws TwitterApiException Error on getting Oauth token
     */
    private TwitterApi getTwitterApiInstance(TwitterAuthenticator authenticator) throws TwitterApiException {
        try {
            return new TwitterApi(new TwitterCredentialsBearer(authenticator.getBearerToken()));
        } catch (TwitterAuthenticationException e) {
            throw new TwitterApiException("Error on getting Oauth token", e);
        }
    }

    /**
     * Adding tweet fields
     *
     * @return List of fields
     */
    private Set<String> getTweetFields() {
        Set<String> tweetFields = new HashSet<>();
        tweetFields.add("id");
        tweetFields.add("created_at");
        tweetFields.add("text");
        tweetFields.add("author_id");
        return tweetFields;
    }

    /**
     * Adding user fields
     *
     * @return List of fields
     */
    private Set<String> getUserFields() {
        Set<String> userFields = new HashSet<>();
        userFields.add("id");
        userFields.add("created_at");
        userFields.add("name");
        userFields.add("username");
        return userFields;
    }


}
