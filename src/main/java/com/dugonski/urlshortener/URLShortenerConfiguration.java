package com.dugonski.urlshortener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class URLShortenerConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
