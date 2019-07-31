package com.dugonski.urlshortener.impl.service;

import com.dugonski.urlshortener.api.model.ShortenedURL;
import com.dugonski.urlshortener.api.service.URLService;
import com.dugonski.urlshortener.impl.component.Shortener;
import com.dugonski.urlshortener.impl.repository.URLRepository;
import com.dugonski.urlshortener.impl.model.URLEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@Service
public class URLServiceImpl implements URLService {

    @Autowired
    private Clock clock;

    @Autowired
    private URLRepository repository;

    @Autowired
    private Shortener shortener;

    @Value("#{systemProperties['expirationTimeMillis'] ?: 60000}")
    private Long expiresAt;

    @Override
    public ShortenedURL persist(URL url) {
        URLEntity urlEntity = new URLEntity();
        urlEntity.setUrl(url.toString());
        urlEntity.setExpiresAt(this.clock.millis() + this.expiresAt);
        urlEntity.setHash(this.shortener.transform(UUID.randomUUID().toString()));
        return this.repository.save(urlEntity);
    }

    @Override
    public Optional<ShortenedURL> retrieveURL(String hash) {
        return this.repository.findById(hash).map(ShortenedURL.class::cast);
    }

    @Override
    public Optional<URL> safeConvertURL(String url) {
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    @Override
    public void setExpiresAt(long millis) {
        this.expiresAt = millis;
    }

    @Override
    public long getExpiresAt() {
        return this.expiresAt;
    }
}
