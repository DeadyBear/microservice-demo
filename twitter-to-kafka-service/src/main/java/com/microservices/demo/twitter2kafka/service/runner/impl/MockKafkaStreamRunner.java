package com.microservices.demo.twitter2kafka.service.runner.impl;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter2kafka.service.exception.TwitterToKafkaServiceException;
import com.microservices.demo.twitter2kafka.service.listener.TwitterListener;
import com.microservices.demo.twitter2kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets" , havingValue = "true")
@SuppressWarnings("unused")
public class MockKafkaStreamRunner implements StreamRunner {
    private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterListener twitterListener;

    private static final Random random= new Random();

    private static final String[] WORDS = new String[]{"why", "who", "gay", "rattle snake", "about", "that", "Kanye West", "suck", "fuck", "penis",
            "large", "small", "has a", "dog", "banana", "pokemon", "bahi", "do you know the way", "a", "segs", "bitch", "spartan"
    };

    private static final String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";


    public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, TwitterListener twitterListener) {
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.twitterListener = twitterListener;
    }

    @Override
    public void start(){
        String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        Integer maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetsLength();
        Integer minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetsLength();
        Long mockSleepMs = twitterToKafkaServiceConfigData.getMockSleepMs();
        LOG.info("Starting mock filttering twitter stream for keywords.... {}", Arrays.toString(keywords));
        SimulateTwitterStream(keywords, maxTweetLength, minTweetLength, mockSleepMs);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void SimulateTwitterStream(String[] keywords, Integer maxTweetLength, Integer minTweetLength, Long mockSleepMs) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while(true){
                    String formattedTweetRawJson = getFormattedTweets(keywords, minTweetLength, maxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetRawJson);
                    twitterListener.onStatus(status);
                    sleep(mockSleepMs);
                }
            } catch (TwitterException e) {
                LOG.error("Error creating twitter status!.. " , e);
            }
        });

    }

    private void sleep(Long mockSleepMs) {
        try {
            Thread.sleep(mockSleepMs);
        } catch (InterruptedException e) {
            throw new TwitterToKafkaServiceException("Error while sleeping zzzzc....");
        }
    }

    private String getFormattedTweets(String[] keywords, Integer minTweetLength, Integer maxTweetLength) {
        String[] params = new String[]{
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords,minTweetLength,maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        return FormatTweetAsJsonWithParams(params);
    }

    private static String FormatTweetAsJsonWithParams(String[] params) {
        String tweet = tweetAsRawJson;
        for(int i = 0; i< params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, Integer minTweetLength, Integer maxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = random.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
        return constructRandomTweets(keywords, tweet, tweetLength);
    }

    private static String constructRandomTweets(String[] keywords, StringBuilder tweet, int tweetLength) {
        for (int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[random.nextInt(WORDS.length)]).append(" ");
            if (i == tweetLength / 2) {
                tweet.append(keywords[random.nextInt(keywords.length)]).append(" ");
            }
        }
        return tweet.toString().trim();
    }

}

