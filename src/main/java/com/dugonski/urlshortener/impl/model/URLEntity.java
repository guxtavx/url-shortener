package com.dugonski.urlshortener.impl.model;

import com.dugonski.urlshortener.api.model.CreatedUrl;
import com.dugonski.urlshortener.api.model.ShortenedURL;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Clock;

@Data
@NoArgsConstructor
@Entity
public class URLEntity implements ShortenedURL {

    @Id
    private String hash;
    private String url;
    private long expiresAt;

    @Override
    public CreatedUrl toCreatedUrl(String baseUrl) {
        CreatedUrl createdUrl = new CreatedUrl();
        createdUrl.setNewUrl(String.format("%s/%s", baseUrl, this.hash));
        createdUrl.setExpiresAt(this.expiresAt);
        return createdUrl;
    }

    @Override
    public boolean isExpired(Clock clock) {
        return clock.millis() > this.expiresAt;
    }

}
