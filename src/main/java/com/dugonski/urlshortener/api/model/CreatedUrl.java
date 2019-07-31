package com.dugonski.urlshortener.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreatedUrl {

    private String newUrl;
    private long expiresAt;

}
