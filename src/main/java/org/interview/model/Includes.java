package org.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public record Includes(ArrayList<User> users) {
    public record User(@JsonProperty("created_at")
            ZonedDateTime createdAt,
            String id,
            String name,
            String username) {
    }
}
