package org.interview.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.interview.model.DTO.StatisticDTO;
import org.interview.model.DTO.TweetDTO;
import org.interview.model.DTO.TweetListDTO;
import org.interview.model.Tweet;
import org.interview.service.FileExportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Service
public class ProcessorServiceImpl implements org.interview.service.ProcessorService {

    @Value("${tweets-count}")
    private int tweetsCount;

    @Value("${time-limit}")
    private int timeLimit;

    private final FileExportService fileExportService;

    public ProcessorServiceImpl(FileExportService fileExportService) {
        this.fileExportService = fileExportService;
    }

    @Override
    public void getTweets(InputStream inputStream) throws IOException {

        log.info("Getting stream of tweets");
        Gson gson = getGson();
        Set<TweetListDTO> tweets = new TreeSet<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        int counter = 0;
        while ((line = in.readLine()) != null && timeLimitCheck(startTime) && counter < tweetsCount) {
            if (!line.isEmpty()) {
                Tweet tweet = gson.fromJson(line, Tweet.class);
                Optional<TweetListDTO> first = tweets.stream().filter(tweetListDTO -> tweetListDTO.getUser().equals(tweet.getUser())).findFirst();
                if (first.isPresent()) {
                    first.get().addTweets(new TweetDTO().mapper(tweet));
                } else {
                    tweets.add(new TweetListDTO().withUser(tweet.getUser()).withTweets(Collections.singleton(new TweetDTO().mapper(tweet))));
                }
                counter++;
                endTime = System.currentTimeMillis();
            }
        }

        fileExportService.createJsonTweetFile(tweets, gson);
        fileExportService.createJsonStatisticFile(new StatisticDTO(counter, (int) ((endTime - startTime) / 1000L)), gson);
    }

    private Gson getGson() {
        return new GsonBuilder().setDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy").create();
    }

    private boolean timeLimitCheck(long startTime) {
        return ((System.currentTimeMillis() - startTime) / 1000L) < timeLimit;
    }
}
