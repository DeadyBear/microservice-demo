package com.microservices.demo.twitter2kafka.service;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter2kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

//Implementing using command line runner
@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
public class TwitterToKafkaServiceApplication implements CommandLineRunner
{
    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
    //Constructing injection
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final StreamRunner streamRunner;
    public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData, StreamRunner streamRunner) {
        this.twitterToKafkaServiceConfigData = configData;
        this.streamRunner = streamRunner;
    }

//    @Autowired  //field injection
//    private TwitterToKafkaServiceConfigData



    public static void main(String[] args)
    {SpringApplication.run(TwitterToKafkaServiceApplication.class,args);}

    @Override
    public void run(String... args) throws Exception {
        LOG.info("App Starts...");
        LOG.info(Arrays.toString(twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[] {})));
        LOG.info(twitterToKafkaServiceConfigData.getWelcomeMessage());
        streamRunner.start();
    }
}

//NOT A GOOD OPTION
//@SpringBootApplication
//@Scope("request") //create a new bean for each request
//public class TwitterToKafkaServiceApplication {
//    public static void main(String[] args)
//    {
//        SpringApplication.run(TwitterToKafkaServiceApplication.class,args);
//    }
//    //NOt a good option
//    //Generates a bean for Spring-boot application once
//    @PostConstruct
//    public void init()
//    {
//    }
//}

/*
  WIll generate beans on Application Event. Will run once for sure
 */
// @SpringBootApplication
// public class TwitterToKafkaServiceApplication implements ApplicationListener {
//     public static void main(String[] args)
//     {
//         SpringApplication.run(TwitterToKafkaServiceApplication.class,args);
//     }
//     @Override
//     public void onApplicationEvent(ApplicationEvent applicationEvent)
//     {
//
//     }
// }

