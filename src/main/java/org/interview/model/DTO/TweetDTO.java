package org.interview.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.interview.model.Tweet;

import java.util.Date;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@With
public class TweetDTO {

    private String messageId;

    private Date creationDate;

    private String message;


    public TweetDTO mapper(Tweet tweet) {
        this.message = tweet.getMessage();
        this.creationDate = tweet.getCreationDate();
        this.messageId = tweet.getMessageId();
        return this;
    }
}
