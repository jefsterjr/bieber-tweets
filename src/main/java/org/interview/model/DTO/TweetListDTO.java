package org.interview.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.interview.model.User;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@With
public class TweetListDTO implements Comparable<TweetListDTO> {

    private User user;
    private Set<TweetDTO> tweets;

    public void addTweets(TweetDTO tweet) {
        if (this.tweets == null) {
            this.tweets = new TreeSet<>(Comparator.comparing(TweetDTO::getCreationDate));
            this.tweets.add(tweet);
        } else {
            Set<TweetDTO> newSet = new TreeSet<>(Comparator.comparing(TweetDTO::getCreationDate));
            newSet.addAll(tweets);
            newSet.add(tweet);
            this.tweets = newSet;
        }
    }

    @Override
    public int compareTo(TweetListDTO o) {
        return this.getUser().getCreationDate().compareTo(o.getUser().getCreationDate());
    }
}
