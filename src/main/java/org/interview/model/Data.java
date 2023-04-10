package org.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public record Data(
        @JsonProperty("author_id") String authorId,
        @JsonProperty("created_at") ZonedDateTime createdAt,
        String id,
        String text,
        @JsonProperty("edit_history_tweet_ids") ArrayList<String> editHistoryTweetIds) {

}

