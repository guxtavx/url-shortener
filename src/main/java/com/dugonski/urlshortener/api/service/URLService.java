package com.dugonski.urlshortener.api.service;

import com.dugonski.urlshortener.api.model.ShortenedURL;

import java.net.URL;
import java.util.Optional;

public interface URLService {

    ShortenedURL persist(URL url);

    Optional<ShortenedURL> retrieveURL(String hash);

    Optional<URL> safeConvertURL(String url);

    void setExpiresAt(long millis);

    long getExpiresAt();
}
