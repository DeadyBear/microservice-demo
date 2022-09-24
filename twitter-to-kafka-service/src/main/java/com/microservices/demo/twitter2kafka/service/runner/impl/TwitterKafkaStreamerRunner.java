package com.microservices.demo.twitter2kafka.service.runner.impl;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter2kafka.service.listener.TwitterListener;
import com.microservices.demo.twitter2kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.*;

import javax.annotation.PreDestroy;
import java.util.Arrays;

@SuppressWarnings("unused")
@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets" , havingValue = "false")
public class TwitterKafkaStreamerRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamerRunner.class);
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterListener twitterListener;
    private TwitterStream twitterStream;

    public TwitterKafkaStreamerRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, TwitterListener twitterListener) {
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.twitterListener = twitterListener;
    }

    @Override
    public void start(){
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(twitterListener);
        addFilter();

    }
    @PreDestroy
    public void shutdown()
    {
        if(twitterStream != null)
        {
            LOG.info("Stream is now closing!...");
            twitterStream.shutdown();
        }
    }

    private void addFilter() {
        String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        LOG.info("Started filtering twitter keywords{}" , Arrays.toString(keywords));
    }
}
