package com.dugonski.urlshortener.api.model;

import java.time.Clock;

public interface ShortenedURL {

    CreatedUrl toCreatedUrl(String baseUrl);

    String getUrl();

    boolean isExpired(Clock clock);

}
