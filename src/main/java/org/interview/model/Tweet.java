package org.interview.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Tweet implements Comparable<Tweet> {

    @SerializedName("id_str")
    private String messageId;

    @SerializedName("created_at")
    private Date creationDate;

    @SerializedName("text")
    private String message;

    @SerializedName("user")
    private User user;

    @Override
    public int compareTo(Tweet o) {
        return creationDate.compareTo(o.getCreationDate());
    }
}
