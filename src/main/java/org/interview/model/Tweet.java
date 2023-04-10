package org.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Tweet(Data data, Includes includes, @JsonProperty("matching_rules") List<MatchingRules> matchingRules) {
}
