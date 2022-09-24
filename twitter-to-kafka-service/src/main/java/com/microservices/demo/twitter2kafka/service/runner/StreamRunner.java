package com.microservices.demo.twitter2kafka.service.runner;

import twitter4j.TwitterException;

public interface StreamRunner {
    void start() throws TwitterException;
}
